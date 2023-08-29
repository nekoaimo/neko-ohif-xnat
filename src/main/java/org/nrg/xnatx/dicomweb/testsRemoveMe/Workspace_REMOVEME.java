package org.nrg.xnatx.dicomweb.testsRemoveMe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.UID;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;

public class Workspace_REMOVEME
{
	public static void main(String[] args)
	{
		String url = "jdbc:postgresql://localhost/pacsdb";
		String user = "pacs";
		String password = "pacs";

		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to the PostgreSQL server successfully.");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM dicomattrs WHERE pk = 1;" );
			while ( rs.next() ) {
				int id = rs.getInt("pk");
				byte[] attrs = rs.getBytes("attrs");

				Attributes result = new Attributes();

				ByteArrayInputStream is = new ByteArrayInputStream(attrs);
				DicomInputStream dis = new DicomInputStream(is);

				dis.readFileMetaInformation();
				dis.readAttributes(result, -1, -1);

				System.out.print(result.toString());

				// Try to re-encode
				ByteArrayOutputStream out = new ByteArrayOutputStream(512);
				DicomOutputStream dos = new DicomOutputStream(out, UID.DeflatedExplicitVRLittleEndian);
				dos.writeDataset(null, result);
				byte[] reEncoded = out.toByteArray();

				int x = 10;
			}

			rs.close();
			stmt.close();
			conn.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}  catch (IOException e) {
			e.printStackTrace();
		}


	}
}
