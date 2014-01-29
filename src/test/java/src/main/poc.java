package src.main;

import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.jaxb.v201309.*;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponse;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.v201309.ReportDownloader;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.utils.Streams;
import com.google.api.client.auth.oauth2.Credential;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileOutputStream;

public class poc
{

    public static void main(String[] args) throws Exception
    {
        //
        // Ces 6 variables sont à binder avec la base SQL
        //
        final String refreshToken = "";
        final String clientId = "";
        final String clientSecret = "";
        final String clientCustomerId = "";
        final String userAgent = "";
        final String developerToken = "";

        // On crée les credentials, puis la session avec les variables qu'on a récupéré de la DB
        final Credential credential = new OfflineCredentials.Builder()
                .forApi(OfflineCredentials.Api.ADWORDS)
                .withClientSecrets(clientId, clientSecret)
                .withRefreshToken(refreshToken)
                .build()
                .generateCredential();

        final AdWordsSession adWordsSession = new AdWordsSession.Builder()
                .withClientCustomerId(clientCustomerId)
                .withDeveloperToken(developerToken)
                .withUserAgent(userAgent)
                .withOAuth2Credential(credential)
                .build();

        // Récupération du rapport dans un fichier CSV dans le répertoire courant
        String reportFile = "report.csv";
        runExample(adWordsSession, reportFile);
    }

    public static void runExample(AdWordsSession session, String reportFile) throws Exception
    {
        // Create selector.
        Selector selector = new Selector();
        selector.getFields().addAll(Lists.newArrayList("CampaignId",
                "AdGroupId",
                "Id",
                "CriteriaType",
                "Criteria",
                "Impressions",
                "Clicks",
                "Cost"));

        // Create report definition.
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setReportName("Criteria performance report #" + System.currentTimeMillis());
        reportDefinition.setDateRangeType(ReportDefinitionDateRangeType.YESTERDAY);
        reportDefinition.setReportType(ReportDefinitionReportType.CRITERIA_PERFORMANCE_REPORT);
        reportDefinition.setDownloadFormat(DownloadFormat.CSV);
        // Enable to allow rows with zero impressions to show.
        reportDefinition.setIncludeZeroImpressions(false);
        reportDefinition.setSelector(selector);

        try
        {
            // Set the property api.adwords.reportDownloadTimeout or call
            // ReportDownloader.setReportDownloadTimeout to set a timeout (in milliseconds)
            // for CONNECT and READ in report downloads.
            ReportDownloadResponse response =
                    new ReportDownloader(session).downloadReport(reportDefinition);
            FileOutputStream fos = new FileOutputStream(new File(reportFile));
            Streams.copy(response.getInputStream(), fos);
            fos.close();
            System.out.println("Report successfully downloaded: " + reportFile);
        }
        catch (ReportDownloadResponseException e)
        {
            System.out.println("Report was not downloaded. " + e);
        }
    }
}
