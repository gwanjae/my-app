import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;

public class FileListTest {

	public static void main(String[] args) {
		File applicationPath = new File(System.getProperty("user.home") + File.separator + ".ngenebio_analysys_gui" + File.separator + "application");
		FileFilter fileFilter = new WildcardFileFilter("*.jar");
		File[] jarFiles = applicationPath.listFiles(fileFilter);

		if (jarFiles != null && jarFiles.length > 0) {
			for (File file : jarFiles) {
				System.out.println(file.getAbsolutePath());
			}
		}
	}

}
