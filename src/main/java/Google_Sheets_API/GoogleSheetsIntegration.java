package Google_Sheets_API;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.services.sheets.v4.SheetsScopes;import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GoogleSheetsIntegration {

    private static final String SPREADSHEET_ID = "1cGAtLLIn3yE_eIIWIZi05tlxnLu74xkoBDAEmcATOm0"; //sheet ID
    private static final String RANGE = "A:B";

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {

        InputStream jsonKeyStream = GoogleSheetsIntegration.class.getResourceAsStream("/skimmi-1ebc20ea8b70.json");

        if (jsonKeyStream == null) {
            throw new FileNotFoundException("JSON key file not found.");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(jsonKeyStream)
                .createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("skimmi")
                .build();
    }

    public static void writeToSheet(String header, String value) throws IOException, GeneralSecurityException {

//        try {
//            // Load JSON key file and create credentials
//            Sheets sheetsService = getSheetsService();
//        // Prepare values to be written
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String timestamp = dateFormat.format(new Date());
//
//        List<List<Object>> values = Arrays.asList(Arrays.asList(timestamp, value));
//
//        // Build the request
//        ValueRange body = new ValueRange().setValues(values);
//        sheetsService.spreadsheets().values()
//                .append(SPREADSHEET_ID, RANGE, body)
//                .setValueInputOption("RAW")
//                .execute();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
        try {
            // Load JSON key file and create credentials
            Sheets sheetsService = getSheetsService();

            // Find the column number under the specified header
            int columnNumber = findRowNumber(sheetsService, SPREADSHEET_ID, header);

            if (columnNumber == -1) {
                System.out.println("Header not found.");
                return;
            }

            // Find the last used row in the specified column
            int lastUsedRow = getLastUsedRow(sheetsService, SPREADSHEET_ID, columnNumber);

            // Prepare values to be written
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());

            // Build the range based on the found column number and last used row
            String range = getColumnLetter(columnNumber) + (lastUsedRow + 1);

            // Create the ValueRange object to append the values under the specified header
            List<List<Object>> values = Arrays.asList(Arrays.asList(timestamp, value));
            ValueRange body = new ValueRange().setValues(values);

            // Build the request
            sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, range, body)
                    .setValueInputOption("RAW")
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getLastUsedRow(Sheets sheetsService, String spreadsheetId, int columnNumber) throws IOException {
        // Find the last used row in the specified column
        String range = getColumnLetter(columnNumber) + ":" + getColumnLetter(columnNumber);
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();

        return values != null ? values.size() : 0;
    }

    private static String getColumnLetter(int columnNumber) {
        // Convert the column number to a column letter (e.g., 1 -> A, 2 -> B, ...)
        char columnLetter = (char) ('A' + columnNumber - 1);
        return String.valueOf(columnLetter);
    }

    private static String getRange(int columnNumber) {
        // Convert the column number to a column letter (e.g., 1 -> A, 2 -> B, ...)
        char columnLetter = (char) ('A' + columnNumber - 1);
        return columnLetter + "1";
    }

    private static int findRowNumber(Sheets sheetsService, String spreadsheetId, String header) throws IOException {
        String range = "A1:Z1";  // Assuming headers are in the first row, adjust the range as needed

        // Make the request to get values in the specified range
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return -1;  // Header not found
        } else {
            // Iterate through the headers in the first row to find the column number where the header is located
            List<Object> headers = values.get(0);
            for (int i = 0; i < headers.size(); i++) {
                if (headers.get(i).toString().equals(header)) {
                    return i + 1;  // Return the column number (add 1 because column numbers start from 1)
                }
            }
        }

        System.out.println("Header not found.");
        return -1;  // Header not found
    }


}


//    public static void main(String[] args) {
//        try {
//            // Example: Write "Hello, World!" to the sheet
//            writeToSheet("Hello");
//        } catch (IOException | GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//    }

