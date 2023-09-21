<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:param name="project_name"/>
    <xsl:template match="/">
        <html>
            <body>
                <h2>Project <xsl:value-of select="$project_name"/> groups</h2>
                <table border="1">
                    <tr>
                        <th>Group</th>
                    </tr>
                    <xsl:for-each select="/*[name()='Payload']/*[name()='Projects']/*[name()='Project'][@name=$project_name]/*[name()='Groups']/*[name()='Group']">
                        <tr>
                            <td>
                                <xsl:value-of select="@name"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>