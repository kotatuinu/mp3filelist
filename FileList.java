/**
 * ID3Tag取得プログラム：FileListクラス
 *  指定ディレクトリ以下のファイル一覧を取得するクラス
 *
 * Copyleft 2008, Kotatuinu.
 *
 * title   : ID3Tag Getter
 * author  : 炬燵犬
 * version : 0.1
 * mail    : kotatuinu@nifty.com
 * Website : http://homepage2.nifty.com/kotatuinu/
 * Released: 2008/03/09
 * NOTICE  : 
 * 本プログラムは、商用利用および改造を自由に行ってくださってもかまいません。
 * ただし、javaの更なる発展のため、改造したソースは公開してください。
 * 利用・改造の連絡は不要です。
 * なお、本プログラムの利用によりあなた、またはあなたの周囲に損害が発生しても、
 * 当方は一切関知しません。
 *  - USE THIS PROGRAM AT YOUR OWN RISK -
**/
package mp3filelist;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import mp3filelist.MP3File;

public class FileList {
	private ArrayList objMp3FileList = null;
	private ArrayList objDirList = null;
	private File objFile_;

	public FileList(File objFile) {
		objFile_ = objFile;
	}
	
	public long getFileInfo() throws UnsupportedEncodingException {
		if(objFile_ == null) {
			System.out.print("Argment is null.\n");
			return -1; // error
		}

		String strCurrentPath = objFile_.toString();
		if(!objFile_.isDirectory()) {
			System.out.print("This Path is not Directry. :" + strCurrentPath + "\n");
			return -1;
		}

		objMp3FileList = new ArrayList();
		objDirList = new ArrayList();

		for(int i = 0; objFile_.listFiles().length > i; i++) {

			if(objFile_.listFiles()[i].isDirectory()) {
				FileList objFileList = new FileList(objFile_.listFiles()[i]);
				objFileList.getFileInfo();
				objDirList.add(objFileList);

			} else if(objFile_.listFiles()[i].isFile()) {
				MP3File objMP3File = new MP3File(objFile_.listFiles()[i]);

				objMP3File.getMP3Info();
				objMp3FileList.add(objMP3File);
			}
		}
		
		return 0;
	}
	public Iterator getMP3FileIte() {
		return objMp3FileList.iterator();
	}
	public Iterator getDirIte() {
		return objDirList.iterator();
	}
	public String getDir() {
		return objFile_.getPath();
	}
}
