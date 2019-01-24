package common;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import client.ClientConsole;
import client.MyData;
import javafx.scene.control.Alert.AlertType;

public class Report {
	private int managerID;
	private final Date dateIssued;
	/**
	 * 
	 * @param dfs - Data from server!
	 */
	public Report(MyData dfs)
	{
		this.managerID=(Integer)dfs.getData("managerID");
		this.dateIssued = new Date(System.currentTimeMillis());
		try {
		switch (dfs.getAction()) {
		case "Activity Report":
			ActivityReport((Integer)dfs.getData("active"), (Integer)dfs.getData("inactive"), (Integer)dfs.getData("frozen"), (Integer)dfs.getData("totalCopiesInBorrow"), (Integer)dfs.getData("lateMembers"));
			break;
		 case "Borrow Report":
			 BorrowReport((Integer)dfs.getData("regular"), (Integer)dfs.getData("popular"));
			break;
		 case "Late Return Report":
			 LateReturnReport((HashMap<String,Integer>)dfs.getData("result"));
			break;
		}
		} catch (IOException | DocumentException de) {}
	}
	
	public Date getDateIssued() 
	{
		return dateIssued;
	}

	private void addTableHeader(PdfPTable table,String header1,String header2) {
	    Stream.of(header1,header2)
	      .forEach(columnTitle -> {
	        PdfPCell header = new PdfPCell();
	        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
	        header.setBorderWidth(2);
	        header.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
	        header.setPhrase(new Phrase(columnTitle));
	        header.setRole(PdfName.HEADERS);
	        table.addCell(header);
	    });
	}
	public void addAvg(PdfPTable table) {
		float sum=0,amount=0,avg;
		for (PdfPRow row : table.getRows()) {
			if (row.getCells()[1].getRole().equals(PdfName.INFO)) {
			sum+=Float.parseFloat(row.getCells()[1].getPhrase().getContent());
			amount++;
			}
		}
		avg = sum/amount;
		table.getRows().add(createRow("Average",avg,PdfName.OP));
	}

	public void BorrowReport(int regular, int popular) throws DocumentException, IOException {
		ArrayList<PdfPRow> rows = new ArrayList<>();
		rows.add(createRow("Regular Books",regular));
		rows.add(createRow("Popular Books",popular));
		produceReport("Borrowed Copies Report",rows,"Name","Amount (in days)");
	}
	
	public void LateReturnReport(HashMap<String,Integer> result) throws DocumentException, IOException {
		ArrayList<PdfPRow> rows = new ArrayList<>();
		for (Entry<String, Integer> s : result.entrySet())
			rows.add(createRow(s.getKey(),s.getValue()));
		produceReport("Late Return Report",rows);
	}
	
	public void ActivityReport(int activeMembers, int inactiveMembers,int frozenMembers,
			int totalCopiesInBorrow, int numberOfLateMembers) throws DocumentException, IOException {
		ArrayList<PdfPRow> rows = new ArrayList<>();
		rows.add(createRow("Frozen Members",frozenMembers));
		rows.add(createRow("Active Members",activeMembers));
		rows.add(createRow("In-Active Members",inactiveMembers));
		rows.add(createRow("Late Members",numberOfLateMembers));
		rows.add(createRow("Total borrowed copies",totalCopiesInBorrow));
		produceReport("Activity Report",rows);
	}
	public void addMedian(PdfPTable table) {
		ArrayList<Integer>list=new ArrayList<>();
		for (PdfPRow row : table.getRows())
			if (row.getCells()[1].getRole().equals(PdfName.INFO))
				list.add(Integer.parseInt(row.getCells()[1].getPhrase().getContent()));
		Collections.sort(list);
		table.getRows().add(createRow("Median",list.get(list.size()/2),PdfName.OP));
	}
	
	public int getDecBD() {
		return 0;
	}
	protected PdfPRow createRow(String n,Number v) {
		return createRow(n, v, PdfName.INFO);
	}
	protected PdfPRow createRow(String n, Number v, PdfName rightRole) {
		PdfPCell value=new PdfPCell( new Phrase (String.valueOf(v)));
		if (v instanceof Float)
			value.setPhrase(new Phrase(String.format("%.2f", v)));
		value.setRole(rightRole);
		value.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		PdfPCell name = new PdfPCell( new Phrase (n));
		name.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		name.setRole(PdfName.NAME);
		if (rightRole.equals(PdfName.op)) { // avg,median,decbd
			name.getPhrase().getFont().setStyle(Font.BOLD);
			value.getPhrase().getFont().setStyle(Font.BOLD);
		}
		return new PdfPRow(new PdfPCell[] {name,value});
	}
	public void produceReport(String Title, ArrayList<PdfPRow> rows) throws DocumentException, IOException {
		produceReport(Title, rows,"Name","Amount");
	}
	public void produceReport(String Title, ArrayList<PdfPRow> rows, String header1, String header2) throws DocumentException, IOException {
		Document document = new Document();
		LocalDateTime now = LocalDateTime.now();
		String time = this.dateIssued+"_"+now.getHour()+"-"+now.getMinute()+"-"+now.getSecond();
		File newPDF=new File("./src/client/Reports/"+Title+"_"+this.managerID+"_"+time+".pdf");
		PdfWriter.getInstance(document, new FileOutputStream(newPDF));
		document.open();
		Paragraph title = new Paragraph(Title);
		title.getFont().setSize(36);
		title.setAlignment(Paragraph.ALIGN_CENTER);
		title.getFont().setColor(BaseColor.BLUE);
		document.add(title);
		Paragraph date = new Paragraph(30,time);
		date.setAlignment(Paragraph.ALIGN_CENTER);
		Paragraph manager = new Paragraph(15,"Manager ID: "+this.managerID);
		manager.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(date);
		document.add(manager);
		PdfPTable table = new PdfPTable(2);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		addTableHeader(table,header1,header2);
		for (PdfPRow row : rows)
			table.getRows().add(row);
		addAvg(table);
		addMedian(table);
		document.add(Chunk.NEWLINE);
		document.add(table);
		document.close();
		try {
		if (Desktop.isDesktopSupported())
		     Desktop.getDesktop().open(newPDF);
		} catch (IOException e) {
			if (e.getMessage().contains("section open"))
			ClientConsole.newAlert(AlertType.ERROR, null, "Already opened", "The file is already opened");
		};
	}
	
}
