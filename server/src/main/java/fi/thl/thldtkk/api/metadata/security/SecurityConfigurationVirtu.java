package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.security.virtu.VirtuSamlUserDetailsService;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.service.UserProfileService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLProcessor;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

@Configuration
@Profile("virtu")
public class SecurityConfigurationVirtu extends WebSecurityConfigurerAdapter {

  @Value("${virtu.entityId}")
  private String entityId;

  @Value("${virtu.entityBaseUrl}")
  private String entityBaseUrl;

  @Value("${virtu.samlKeystoreResource}")
  private Resource samlKeystoreResource;

  @Value("${virtu.samlKeystorePassword}")
  private String samlKeystorePassword;

  @Value("${virtu.spCertificateAliasInKeystore}")
  private String spCertificateAliasInKeystore;

  @Value("${virtu.spCertificatePassword}")
  private String spCertificatePassword;

  @Value("${virtu.metadataUrl}")
  private String virtuMetadataUrl;

  @Value("${virtu.metadataSigningCertificateAliases}")
  private String[] virtuMetadataSigningCertificateAliases;

  @Value("${virtu.idpDiscoveryServiceUrl}")
  private String idpDiscoveryServiceUrl;

  @Autowired
  private UserProfileService userProfileService;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  public void configureAuthenticationManager(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    authManagerBuilder.authenticationProvider(samlAuthenticationProvider());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
      .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class);
  }

  @Bean
  public MetadataGeneratorFilter metadataGeneratorFilter() throws MetadataProviderException {
    MetadataGeneratorFilter metadataGeneratorFilter = new MetadataGeneratorFilter(spMetadataGenerator());
    metadataGeneratorFilter.setManager(metadataManager());
    return metadataGeneratorFilter;
  }

  @Bean
  public MetadataGenerator spMetadataGenerator() {
    MetadataGenerator generator = new MetadataGenerator();
    generator.setEntityId(entityId);
    generator.setEntityBaseURL(entityBaseUrl);
    generator.setKeyManager(samlKeyManager());
    generator.setExtendedMetadata(spExtendedMetadata());
    generator.setRequestSigned(true);
    generator.setWantAssertionSigned(true);
    generator.setIncludeDiscoveryExtension(true);
    return generator;
  }

  @Bean
  public KeyManager samlKeyManager() {
    Map<String, String> passwords = new LinkedHashMap<>();
    passwords.put(spCertificateAliasInKeystore, spCertificatePassword);

    return new JKSKeyManager(
      samlKeystoreResource,
      samlKeystorePassword,
      passwords,
      spCertificateAliasInKeystore
    );
  }

  @Bean
  public CachingMetadataManager metadataManager() throws MetadataProviderException {
    return new CachingMetadataManager(Arrays.asList(
      virtuMetadataProvider()));
  }

  @Bean
  public MetadataProvider virtuMetadataProvider() throws MetadataProviderException {
    HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(
      backgroundTaskTimer(), httpClient(), virtuMetadataUrl);
    httpMetadataProvider.setParserPool(parserPool());

    ExtendedMetadataDelegate extendedMetadataDelegate =
      new ExtendedMetadataDelegate(httpMetadataProvider, spExtendedMetadata());

    extendedMetadataDelegate.setMetadataTrustCheck(true);
    extendedMetadataDelegate.setMetadataRequireSignature(true);
    extendedMetadataDelegate.setMetadataTrustedKeys(
      new HashSet<>(Arrays.asList(virtuMetadataSigningCertificateAliases)));

    backgroundTaskTimer().purge();

    return extendedMetadataDelegate;
  }

  @Bean
  public Timer backgroundTaskTimer() {
    return new Timer(true);
  }

  @Bean
  public HttpClient httpClient() {
    return new HttpClient(httpConnectionManager());
  }

  @Bean
  public MultiThreadedHttpConnectionManager httpConnectionManager() {
    return new MultiThreadedHttpConnectionManager();
  }

  @Bean
  public FilterChainProxy samlFilter() throws Exception {
    List<SecurityFilterChain> chains = new ArrayList<>();
    chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint()));
    chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"), metadataDisplayFilter()));
    chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"), samlWebSSOProcessingFilter()));
    chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/Virtu/SAML2/POST/**"), samlWebSSOProcessingFilter()));
    return new FilterChainProxy(chains);
  }

  @Bean
  public static SAMLBootstrap samlBootstrap() {
    return new SAMLBootstrap();
  }

  @Bean(name = "parserPoolHolder")
  public ParserPoolHolder parserPoolHolder() {
    ParserPoolHolder holder = new ParserPoolHolder();
    holder.setParserPool(parserPool());
    return holder;
  }

  @Bean(initMethod = "initialize")
  public StaticBasicParserPool parserPool() {
    return new StaticBasicParserPool();
  }

  @Bean
  public ExtendedMetadata spExtendedMetadata() {
    ExtendedMetadata extendedMetadata = new ExtendedMetadata();

    extendedMetadata.setSignMetadata(true);
    extendedMetadata.setSigningKey(spCertificateAliasInKeystore);

    extendedMetadata.setIdpDiscoveryEnabled(true);
    extendedMetadata.setIdpDiscoveryURL(idpDiscoveryServiceUrl);
    extendedMetadata.setIdpDiscoveryResponseURL(entityBaseUrl + "/saml/login");

    extendedMetadata.setEcpEnabled(false);

    return extendedMetadata;
  }

  @Bean
  public MetadataDisplayFilter metadataDisplayFilter() throws MetadataProviderException {
    MetadataDisplayFilter filter = new MetadataDisplayFilter();
    filter.setManager(metadataManager());
    filter.setKeyManager(samlKeyManager());
    filter.setContextProvider(samlContextProvider());
    return filter;
  }

  @Bean
  public SAMLContextProviderImpl samlContextProvider() {
    return new SAMLContextProviderImpl();
  }

  @Bean
  public SAMLAuthenticationProvider samlAuthenticationProvider() {
    SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
    samlAuthenticationProvider.setConsumer(webSSOprofileConsumer());
    samlAuthenticationProvider.setHokConsumer(hokWebSSOprofileConsumer());
    samlAuthenticationProvider.setUserDetails(samlUserDetailsService());
    samlAuthenticationProvider.setSamlLogger(samlLogger());
    samlAuthenticationProvider.setForcePrincipalAsString(false);
    return samlAuthenticationProvider;
  }

  @Bean
  public WebSSOProfileConsumer webSSOprofileConsumer() {
    return new WebSSOProfileConsumerImpl();
  }

  @Bean
  public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
    return new WebSSOProfileConsumerHoKImpl();
  }

  @Bean
  public SAMLUserDetailsService samlUserDetailsService() {
    return new VirtuSamlUserDetailsService(userProfileService, organizationService);
  }

  @Bean
  public SAMLDefaultLogger samlLogger() {
    return new SAMLDefaultLogger();
  }

  @Bean
  public SAMLEntryPoint samlEntryPoint() throws MetadataProviderException {
    SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
    samlEntryPoint.setWebSSOprofile(webSSOprofile());
    samlEntryPoint.setMetadata(metadataManager());
    samlEntryPoint.setSamlLogger(samlLogger());
    samlEntryPoint.setContextProvider(samlContextProvider());
    samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
    return samlEntryPoint;
  }

  @Bean
  public WebSSOProfile webSSOprofile() {
    return new WebSSOProfileImpl();
  }

  @Bean
  public WebSSOProfileOptions defaultWebSSOProfileOptions() {
    WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
    webSSOProfileOptions.setIncludeScoping(false);
    return webSSOProfileOptions;
  }

  @Bean
  public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
    SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
    // TODO: Replace this with default value ("/saml/SSO") when registering QA and prod to actual Virtu.
    samlWebSSOProcessingFilter.setFilterProcessesUrl("/Virtu/SAML2/POST");
    samlWebSSOProcessingFilter.setSAMLProcessor(samlProcessor());
    samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
    samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(
      new SimpleUrlAuthenticationSuccessHandler("/"));
    samlWebSSOProcessingFilter.setAuthenticationFailureHandler(
      new SimpleUrlAuthenticationFailureHandler("/login?virtuLoginError=true"));
    return samlWebSSOProcessingFilter;
  }

  @Bean
  public SAMLProcessor samlProcessor() {
    return new SAMLProcessorImpl(Arrays.asList(
      httpRedirectDeflateBinding(),
      httpPostBinding()
    ));
  }

  @Bean
  public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
    return new HTTPRedirectDeflateBinding(parserPool());
  }

  @Bean
  public HTTPPostBinding httpPostBinding() {
    return new HTTPPostBinding(parserPool(), velocityEngine());
  }

  @Bean
  public VelocityEngine velocityEngine() {
    return VelocityFactory.getEngine();
  }

  @PreDestroy
  public void cleanBackgroundTimerAndConnectionManager() {
    backgroundTaskTimer().purge();
    backgroundTaskTimer().cancel();
    httpConnectionManager().shutdown();
  }

}
