/**
 * ID3Tag取得プログラム：MP3Infoクラス
 *  ID3TagのVer1.0/1.1とVer2.3 取得クラスのスーパークラス
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;


abstract class MP3Info {
	protected String strPath_ = "";
	protected String strFileName_ = "";
	static protected File objFile_ = null;

	/**
	 * ファイル情報:パスを取得
	 */
	public String getPath() {
		return objFile_.getPath();
	}

	/**
	 * ファイル情報:ファイル名を取得
	 */
	public String getFileName() {
		return objFile_.getName();
	}

	/**
	 * ファイル情報:ファイルサイズを取得
	 */
	public long getFileSize() {
		return objFile_.length();
	}

	/**
	 * ファイル情報:最終更新日時を取得
	 */
	public String getLastModified() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date(objFile_.lastModified()));
	}

	/**
	 * ID3:タイトルを取得
	 */
	abstract public String getTitle();

	/**
	 * ID3:アーティスト名を取得
	 */
	abstract public String getArtist();

	/**
	 * ID3:アルバム名を取得
	 */
	abstract public String getAlbum();

	/**
	 * ID3:作成年を取得
	 */
	abstract public String getYear();

	/**
	 * ID3:コメントを取得
	 */
	abstract public String getComment();

	/**
	 * ID3:トラック番号を取得(v1.1以降)
	 */
	abstract public long getTrackNo();

	/**
	 * ID3:ジャンルを取得
	 */
	abstract public long getGenre();

	/**
	 * ID3:タグバージョンを取得
	 */
	abstract public String getTagVersion();

	/**
	 * ID3を取得
	 */
	abstract public void getMP3Info(File objFile) throws UnsupportedEncodingException;

	/**
	 * バイト単位でファイルを読む
	 * @param objFile   読み込み対象ファイル
	 * @param iStartPos 読み込み開始位置
	 * @param iLength   読み込み長
	 * @return 読み込んだデータ(byte配列)
	 */
	static protected byte[] readFile(File objFile, int iStartPos, int iLength) {

		FileInputStream objFIS = null;
		byte byteBuff[] = null;

		if(objFile.length() < iStartPos + iLength) {
			return null;
		}

		try {
			objFIS = new FileInputStream(objFile);
		} catch (FileNotFoundException e) {
//				System.out.print("MP3Info::readFile() " + objFile.getName() + "\n");
//				e.printStackTrace();
			try {
				objFIS.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}

		try {
			byteBuff = new byte[iLength];
			objFIS.skip(iStartPos);
			objFIS.read(byteBuff, 0, iLength);
			objFIS.close();
		} catch (IOException e) {
			System.out.print("MP3Info::readFileMP3V1() " + objFile.getName() + "\n");
			e.printStackTrace();
			return null;
		}

		return byteBuff;

	}
}


