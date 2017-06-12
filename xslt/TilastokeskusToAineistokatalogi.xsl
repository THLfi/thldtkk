<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <Dataset>
            <xsl:apply-templates select="/statmetadoc"/>
        </Dataset>
    </xsl:template>

    <xsl:template match="/statmetadoc">
        <xsl:apply-templates select="docmeta"/>
        <xsl:apply-templates select="statmeta"/>
    </xsl:template>

    <xsl:template match="docmeta">
        <xsl:apply-templates select="subjectgrp"/>
        <xsl:apply-templates select="contentdescriptiongrp"/>
        <xsl:apply-templates select="resourcerelationgrp"/>
        <xsl:apply-templates select="publisher"/>
    </xsl:template>

    <xsl:template match="subjectgrp">
        <xsl:for-each select="subject">
            <xsl:element name="prefLabel">
                <xsl:element name="{@*[1]}">
                    <xsl:value-of select="current()"/>
                </xsl:element>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="resourcerelationgrp">
        <links>
            <xsl:for-each select="resourcerelation">
                <xsl:if test="current()/link">
                    <Link>
                        <prefLabel>
                            <xsl:element name="{@*[1]}">
                                <xsl:value-of select="link/a"/>
                            </xsl:element>
                        </prefLabel>
                        <linkUrl>
                            <xsl:element name="{@*[1]}">
                                <xsl:value-of select="link/a/@href"/>
                            </xsl:element>
                        </linkUrl>
                    </Link>
                </xsl:if>
            </xsl:for-each>
        </links>
    </xsl:template>

    <xsl:template match="contentdescriptiongrp">
        <xsl:for-each select="contentdescription">
            <xsl:element name="description">
                <xsl:element name="{@*[1]}">
                    <xsl:value-of select="current()"/>
                </xsl:element>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="statmeta">
        <instanceVariables>
            <xsl:for-each select="variable">
                <InstanceVariable>
                    <xsl:attribute name="technicalName">
                        <xsl:value-of select="@fieldName"/>
                    </xsl:attribute>
                    <xsl:element name="prefLabel">
                        <xsl:for-each select="variablenamegrp/variablename">
                            <xsl:element name="{@*[1]}">
                                <xsl:value-of select="current()"/>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>
                    <xsl:element name="description">
                        <xsl:for-each select="conceptdefinition/conceptdefgrp/conceptdef">
                            <xsl:element name="{@*[1]}">
                                <xsl:value-of select="current()"/>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>
                </InstanceVariable>
            </xsl:for-each>
        </instanceVariables>
    </xsl:template>
</xsl:stylesheet>
