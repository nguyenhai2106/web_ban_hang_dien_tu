package com.donations.admin.user.exporter;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.swing.text.TabableView;

import com.donations.admin.AbstractExporter;
import com.donations.common.entity.User;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

public class UserPDFExporter extends AbstractExporter {

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/pdf", ".pdf", "users_");
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(16);
		font.setColor(Color.blue);
		Paragraph paragraph = new Paragraph("List Of User", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(paragraph);
		PdfPTable pdfTable = new PdfPTable(6);
		pdfTable.setWidthPercentage(100f);
		pdfTable.setSpacingBefore(8);
		pdfTable.setWidths(new float[] {1f, 6.5f, 2.5f, 2.5f, 2.5f, 2f});
		writeTableHeader(pdfTable);
		writeTableBody(pdfTable, listUsers);
		document.add(pdfTable);
		document.close();
	}

	private void writeTableBody(PdfPTable pdfTable, List<User> litsUsers) {
		litsUsers.stream().forEach(user -> {
			pdfTable.addCell(String.valueOf(user.getId()));
			pdfTable.addCell(String.valueOf(user.getEmail()));
			PdfPCell pdfPCell = new PdfPCell();
			pdfTable.addCell(String.valueOf(user.getFirstName()));
			pdfTable.addCell(String.valueOf(user.getLastName()));
			pdfTable.addCell(String.valueOf(user.getRoles()));
			pdfTable.addCell(String.valueOf(user.isEnabled()));
		});		
	}

	private void writeTableHeader(PdfPTable pdfTable) {
		//Cell format
		PdfPCell pdfPCell = new PdfPCell();
//		pdfPCell.setBackgroundColor(Color.black);
		pdfPCell.setPadding(5);
		pdfPCell.setHorizontalAlignment(Cell.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		//Font format
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(12);
//		font.setColor(Color.white);
		pdfPCell.setPhrase(new Phrase("ID", font));
		pdfTable.addCell(pdfPCell);
		pdfPCell.setPhrase(new Phrase("Email", font));
		pdfTable.addCell(pdfPCell);
		pdfPCell.setPhrase(new Phrase("First Name", font));
		pdfTable.addCell(pdfPCell);
		pdfPCell.setPhrase(new Phrase("Last Name", font));
		pdfTable.addCell(pdfPCell);
		pdfPCell.setPhrase(new Phrase("Roles", font));
		pdfTable.addCell(pdfPCell);
		pdfPCell.setPhrase(new Phrase("Enabled", font));
		pdfTable.addCell(pdfPCell);
	}

}
