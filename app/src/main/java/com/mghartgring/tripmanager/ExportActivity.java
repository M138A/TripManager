package com.mghartgring.tripmanager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.util.ArrayList;

public class ExportActivity extends AppCompatActivity {

    private DatabaseHelper database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_export);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Export trips");
        database = new DatabaseHelper(getBaseContext());
    }

    /**
     * Creates a PdfPCell
     * @param value The value of the cell
     * @param f The front for the cell
     * @return
     */
    private PdfPCell AddCell(String value, Font f)
    {
        PdfPCell cell = new PdfPCell(new Phrase(value, f));
        return cell;
    }

    /**
     * Triggered when the button is clicked
     * @param view The button
     */
    public void BeginExport(View view)
    {
        RemoveOldFile("export.pdf");
        String path = createPDF("export.pdf").toString();
        DisplayPDF(path);
        if(!RemoveTripsAfterExport()) return;
        database.RemoveAll();
    }

    /**
     * Get state of checkbox
     * @return true when checked
     */
    private Boolean RemoveTripsAfterExport(){
        return ((CheckBox) findViewById(R.id.deleteCheckBox)).isChecked();
    }

    /**
     * Rounds to two decimal places
     * @param value
     * @return
     */
    private double ToCurrency(double value)
    {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Creates the PDF document
     * @param fileName The name of the file
     * @return The created file
     */
    private File createPDF(String fileName)
    {
        Document doc = new Document();
        PdfWriter docWriter = null;
        try
        {
            docWriter = PdfWriter.getInstance(doc, openFileOutput(fileName, MODE_APPEND));
            doc.open();

            Paragraph paragraph = new Paragraph("Overview of every trip");

            float[] columnWidths = {1.5f, 1.5f, 1.5f};
            PdfPTable tripsTable = new PdfPTable(columnWidths);
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font stdFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, new BaseColor(0, 0, 0));
            tripsTable.setWidthPercentage(90f);
            tripsTable.addCell(AddCell("TimeStamp", titleFont));
            tripsTable.addCell(AddCell("Trip name", titleFont));
            tripsTable.addCell(AddCell("Distance (meters)", titleFont));
            tripsTable.setHeaderRows(1);

            ArrayList<Trip> Trips = database.GetData();
            double totalDistance = 0.0;
            double cost = 0.0;
            double costPerK = Double.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getAll().get("fuel_price").toString());
            for(int i = 0; i < Trips.size(); i++)
            {
                Trip current = Trips.get(i);
                tripsTable.addCell(AddCell(current.Date, stdFont));
                tripsTable.addCell(AddCell(current.TripName, stdFont));
                tripsTable.addCell(AddCell(String.valueOf(current.Distance), stdFont));
                totalDistance += current.Distance;
            }

            tripsTable.setComplete(true);
            cost = (totalDistance / 1000) * costPerK;
            paragraph.add(tripsTable);
            doc.add(paragraph);

            Paragraph p2 = new Paragraph("The specification of the costs: ", stdFont);
            float[] columnWidthsCosts = {1.5f, 1.5f, 1.5f};
            PdfPTable costTable = new PdfPTable(columnWidthsCosts);
            costTable.addCell(AddCell("Cost per KM", titleFont));
            costTable.addCell(AddCell("Total driven KM", titleFont));
            costTable.addCell(AddCell("Total cost", titleFont));
            costTable.setHeaderRows(1);
            costTable.addCell(AddCell("€" + ToCurrency(costPerK), stdFont));
            costTable.addCell(AddCell(String.valueOf(totalDistance / 1000), stdFont));
            costTable.addCell(AddCell("€" + cost, stdFont));
            costTable.setComplete(true);
            p2.add(costTable);
            doc.add(p2);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            doc.close();
            docWriter.close();
        }
        return getFileStreamPath(fileName);
    }

    /**
     * Removes old files if they exist
     * @param name
     */
    private void RemoveOldFile(String name)
    {
        if(getFileStreamPath(name).exists()) getFileStreamPath(name).delete();
    }

    /**
     * Opens the PDF file
     * @param p Path to the PDF file
     */
    private void DisplayPDF(String p)
    {
        File file = new File(p);
        if (file.exists()) {
            Uri path = GenericFileProvider.getUriForFile(getBaseContext(), "com.mghartgring.tripmanager.provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
