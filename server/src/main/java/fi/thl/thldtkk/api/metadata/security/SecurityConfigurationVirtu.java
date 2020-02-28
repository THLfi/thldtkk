package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.security.log.VirtuSamlLogger;
import fi.thl.thldtkk.api.metadata.security.virtu.VirtuSamlUserDetailsService;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.service.UserProfileService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.EntityRoleFilter;
import org.opensaml.saml2.metadata.provider.FileBackedHTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataFilter;
import org.opensaml.saml2.metadata.provider.MetadataFilterChain;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.SignatureValidationFilter;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.signature.SignatureConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.saml.log.SAMLLogger;
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
import javax.xml.namespace.QName;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static java.util.Arrays.asList;

@Configuration
@Order(3)
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
    return new CachingMetadataManager(asList(
      virtuMetadataProvider()
    )) {
      @Override
      protected void initializeProviderFilters(ExtendedMetadataDelegate provider) throws MetadataProviderException {
        super.initializeProviderFilters(provider);
        MetadataFilter filter = provider.getMetadataFilter();
        if (filter instanceof MetadataFilterChain) {
          MetadataFilterChain filterChain = (MetadataFilterChain) filter;
          // Workaround to problem described in
          // https://github.com/spring-projects/spring-security-saml/issues/160#issuecomment-206391707
          moveSignatureValidationFilterFirst(filterChain.getFilters());
        }
      }

      private void moveSignatureValidationFilterFirst(List<MetadataFilter> filters) {
        Collections.sort(filters, (one, two) -> {
          if (one instanceof SignatureValidationFilter) {
            return -1;
          }
          else if (two instanceof SignatureValidationFilter) {
            return 1;
          }
          return one.getClass().getSimpleName().compareTo(two.getClass().getSimpleName());
        });
      }
    };
  }

  @Bean
  public MetadataProvider virtuMetadataProvider() throws MetadataProviderException {
    String metadataBackupFilePath = new StringBuilder()
      .append(System.getProperty("java.io.tmpdir"))
      .append(File.separator)
      .append("virtu-metadata-backup.xml")
      .toString();

    FileBackedHTTPMetadataProvider httpMetadataProvider = new FileBackedHTTPMetadataProvider(
      backgroundTaskTimer(),
      httpClient(),
      virtuMetadataUrl,
      metadataBackupFilePath);
    httpMetadataProvider.setParserPool(parserPool());

    httpMetadataProvider.setMetadataFilter(virtuMetadataFilters());

    ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(httpMetadataProvider);
    extendedMetadataDelegate.setMetadataRequireSignature(true);
    extendedMetadataDelegate.setMetadataTrustCheck(true);
    extendedMetadataDelegate.setMetadataTrustedKeys(
      new HashSet<>(asList(virtuMetadataSigningCertificateAliases)));

    backgroundTaskTimer().purge();

    return extendedMetadataDelegate;
  }

  private MetadataFilterChain virtuMetadataFilters() {
    // Keep only IdP descriptors in metadata because SP descriptor coming from
    // Virtu metadata interferes with setting from local SP metadata
    QName idpSsoDescriptorElement = new QName("urn:oasis:names:tc:SAML:2.0:metadata", "IDPSSODescriptor");
    EntityRoleFilter idpDescriptorFilter = new EntityRoleFilter(asList(idpSsoDescriptorElement));
    MetadataFilterChain filterChain = new MetadataFilterChain();
    filterChain.setFilters(asList(idpDescriptorFilter));
    return filterChain;
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
    return new FilterChainProxy(chains);
  }

  @Bean
  public static SAMLBootstrap samlBootstrap() {
    return new StrongHashSAMLBootstrap();
  }

  @Bean
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

    extendedMetadata.setRequireArtifactResolveSigned(true);
    extendedMetadata.setSignMetadata(true);
    extendedMetadata.setSigningKey(spCertificateAliasInKeystore);
    extendedMetadata.setSigningAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);

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
  public SAMLLogger samlLogger() {
    return new VirtuSamlLogger();
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
    return new SAMLProcessorImpl(asList(
      httpPostBinding(),
      httpRedirectDeflateBinding()
    ));
  }

  @Bean
  public VelocityEngine velocityEngine() {
    return VelocityFactory.getEngine();
  }

  @Bean
  public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
    return new HTTPRedirectDeflateBinding(parserPool());
  }

  @Bean
  public HTTPPostBinding httpPostBinding() {
    return new HTTPPostBinding(parserPool(), velocityEngine());
  }

  @PreDestroy
  public void cleanBackgroundTimerAndConnectionManager() {
    backgroundTaskTimer().purge();
    backgroundTaskTimer().cancel();
    httpConnectionManager().shutdown();
  }

}
