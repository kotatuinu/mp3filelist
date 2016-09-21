/**
 * ID3Tag取得プログラム：MP3FileListMainクラス
 *  Mainクラス
 *
 * [Usage]
 * コンソールより、MP3のタグ一覧を取得したいディレクトリを引数に指定する。
 * >java mp3filelist/MP3FileListMain <ディレクトリ>
 *
 * 標準出力にID3 tagの内容をcsvっぽい形式で出力します。
 * 出力内容を変更したい場合は、outputFilesメソッドを修正してください。
 * なお、v1.0またはv1.1 と v2.3の両方とも設定されている場合、両者とも出力するようになっています。
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import mp3filelist.FileList;
import mp3filelist.MP3File;

public class MP3FileListMain {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		if(args.length < 1) {
			System.out.print("引数にディレクトリを指定して\n");
			return;
		}
		File objFile = new File(args[0]);
		
		if(!objFile.isDirectory()) {
			System.out.print("引数に指定したのは、ディレクトリではない\n");
			System.out.print("引数にディレクトリを指定して\n");
			return;
		}
		
		FileList objFileList = new FileList(objFile);
		long lRnt = objFileList.getFileInfo();
//		System.out.print(lRnt);
		try {
			MP3Tag.init();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		MP3Tag.outputFiles(objFileList);
	}

	private static class MP3Tag {
	private static long lSeriesNo_ = 0;
	private static long lMaxSeriesNo_ = 0;
	private static long lFileNo_ = 0;
	private static long lMaxFileNo_ = 0;
	private static long lTypeNo_ = 0;
	private static SimpleDateFormat sdf_;
	
	private static int handle1_;
	private static int handle2s_;
	private static int handle21s_;
	private static int handle22s_;
	private static int handle2i_;
	private static int handle3s_;
	private static int handle3i_;
	private static int handle3u_;
	
	private static void init() throws ClassNotFoundException {
		sdf_ = new SimpleDateFormat("yyyyMMddHHmmss");
	}
	
	private static void outputFiles(FileList objFileList) {
		int handle = 0;
		long lSeriesNo = lSeriesNo_;
		MP3File objMP3File;
		ArrayList objMP3List;

//		System.out.print(objFileList.getDir() + "\n");
		Iterator iteMP3File = objFileList.getMP3FileIte();

		while(iteMP3File.hasNext()) {
			objMP3File = (MP3File)iteMP3File.next();
			
			try {
				objMP3List = objMP3File.getMP3Info();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			try {
				for(int i = 0; objMP3List.size() > i; i++) {
					System.out.print(lFileNo_ + ",");
					System.out.print(lTypeNo_ + ",");
					System.out.print(lSeriesNo + ",");

					MP3Info objMP3Info = (MP3Info)objMP3List.get(i);	// 暫定的

					System.out.print(objMP3Info.getLastModified() + ",");
					System.out.print(objMP3Info.getFileSize() + ",");
					System.out.print(objMP3Info.getTagVersion() + ",");
					System.out.print(objMP3Info.getTrackNo() + ",");
					System.out.print(objMP3Info.getTitle() + ",");
					System.out.print(objMP3Info.getGenre() + ",");
					System.out.print(objMP3Info.getArtist() + ",");
					System.out.print(objMP3Info.getComment() + ",");
					System.out.print(objMP3Info.getYear() + ",");
					System.out.print(sdf_.format(new Date()) + ",");
					System.out.print(objMP3Info.getPath() + ",");
					System.out.print(objMP3Info.getFileName() + "\n");
				}

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			lFileNo_++;
		}
		
		Iterator iteDir = objFileList.getDirIte();
		while(iteDir.hasNext()) {
		outputFiles((FileList)iteDir.next());
		}
	}
	}
}
