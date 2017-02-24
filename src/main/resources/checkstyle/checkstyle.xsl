<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">
	<xsl:param name="report-title"/>
	<xsl:template match="/">
		<html>
			<body>
				<table>
					<tr>
						<th colspan="2">
							Javadoc Results - <xsl:value-of select="$report-title"/>
						</th>
					</tr>
					<tr>
						<td>Classes and methods checked</td>
						<td>
							<xsl:number level="any" value="count(descendant::file)"/>
						</td>
					</tr>
					<tr>
						<td>Classes and methods with description</td>
						<td>
							<xsl:number level="any" value="count(descendant::file) - count(descendant::file[error])"/>
						</td>
					</tr>
					<tr>
						<td>Percent documented</td>
						<td>
							<xsl:value-of select='format-number((count(descendant::file) - count(descendant::file[error])) div count(descendant::file), "##.##%")'/>
						</td>
					</tr>
					<tr>
						<td>Generation Date</td>
						<td>
							<dateTimeStamp>
								<xsl:value-of select="java:format(java:java.text.SimpleDateFormat.new('dd-MMM-yyyy HH:mm a z'), java:java.util.Date.new())"/>
							</dateTimeStamp>
						</td>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>