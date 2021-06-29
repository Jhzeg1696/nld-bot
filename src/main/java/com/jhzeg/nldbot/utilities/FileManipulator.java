package com.jhzeg.nldbot.utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

public class FileManipulator {
	
	public static String saveFile(String fileUrl, String senderID)
	{
		String response = "";
		
		try (
			 BufferedInputStream inputStream = new BufferedInputStream(new URL(fileUrl).openStream());
			 FileOutputStream fileOS = new FileOutputStream("/home/jhzeg/Desktop/" + senderID  +".docx");
		    ) 
		{
				byte data[] = new byte[1024];
				int byteContent;
				
			    while ((byteContent = inputStream.read(data, 0, 1024)) != -1) 
			    {
			    	fileOS.write(data, 0, byteContent);
			    }
			    
			    response = "File saved successfully";
			    
			    System.out.println( "Converting file to pdf..." );
			    convertFileToPDF("/home/jhzeg/Desktop/" + senderID + ".docx", "/home/jhzeg/Desktop/" + senderID + ".pdf");
			    System.out.println("File conversation successfull");
			    
			    HttpRequest httpRequest = new HttpRequest();
			    httpRequest.sendImage(senderID, "");
		} 
			
		catch (IOException ioException) 
		{
			System.out.println("An error ocurred in the saveFile method: " + ioException.getMessage());
			response = "Failed to save the file";
		}
		
		return response;
	}
	
	public static void convertFileToPDF(String docPath, String pdfPath) 
	{
	    try 
	    {
	        InputStream doc = new FileInputStream(new File(docPath));
	        XWPFDocument document = new XWPFDocument(doc);
	        PdfOptions options = PdfOptions.create();
	        OutputStream out = new FileOutputStream(new File(pdfPath));
	        PdfConverter.getInstance().convert(document, out, options);
	    }
	    
	    catch (IOException ioException) 
	    {
	        System.out.println(ioException.getMessage());
	    }
	  }
	
}
