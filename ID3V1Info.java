/**
 * ID3Tag取得プログラム：ID3V1Infoクラス
 *  ID3TagのVer1.0/1.1 取得クラス
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
import mp3filelist.MP3Info;

public class ID3V1Info extends MP3Info {
	// MP3 V1
	private String Title_;
	private String Artist_;
	private String Album_;
	private String Year_;
	private String Comment_;
	private long TrackNo_;
	private long Genre_;
	private String TagVer_;


	public ID3V1Info(File objFile) throws UnsupportedEncodingException {
		Title_ = "";
		Artist_ = "";
		Album_ = "";
		Year_ = "";
		Comment_ = "";
		TrackNo_ = 0;
		Genre_ = 0;
		TagVer_ = "";
		getMP3Info(objFile);
	}

	/**
	 * ID3:タイトルを取得
	 */
	public String getTitle() {
		return Title_;
	}

	/**
	 * ID3:アーティスト名を取得
	 */
	public String getArtist() {
		return Artist_;
	}

	/**
	 * ID3:アルバム名を取得
	 */
	public String getAlbum() {
		return Album_;
	}


	/**
	 * ID3:作成年を取得
	 */
	public String getYear() {
		return Year_;
	}

	/**
	 * ID3:コメントを取得
	 */
	public String getComment() {
		return Comment_;
	}

	/**
	 * ID3:トラック番号を取得(v1.1以降)
	 */
	public long getTrackNo() {
		return TrackNo_;
	}


	/**
	 * ID3:ジャンル番号を取得
	 */
	public long getGenre() {
		return Genre_;
	}

	/**
	 * ID3:タグバージョンを取得
	 */
	public String getTagVersion() {
		return TagVer_;
	}

	/**
	 * ID3V1か判定
	 * @note MP3 Tag V1 is Last 128-130Byte data = TAG(0x54, 0x41, 0x47)
	 */
	static public boolean isSettingID3(File objFile) {
		byte byteMP3V1Tag[] = null;

		byteMP3V1Tag = readFile(objFile, (int)(objFile.length()-128), 3);
		if(byteMP3V1Tag != null) {
			if((new String(byteMP3V1Tag, 0, 3)).matches("TAG")) {
				return true;
			}
		}
		return false;
	}


	/*
	TRACK NAME	: 30BYTE
	AIRTIST NAME: 30BYTE
	ALUBM NAME	: 30BYTE
	YEAR		:  4BYTE

	v1
	COMMENT 	: 30BYTE (ID3 Tag v1.1 28BYTE) 
	GENRE		:  1BYTE

	v1.1
	COMMENT 	: 28BYTE
	v1.1flag	:  1BYTE[value Zero]<-125Byte from top
	TRACK NO	:  1BYTE
	GENRE		:  1BYTE
	 */
	public void getMP3Info(File objFile) throws UnsupportedEncodingException {

		if( !isSettingID3(objFile) ) {
			return;
		}

		objFile_ = objFile;

		byte byteMP3V1[] = readFileMP3V1();

		try {
			Title_   = (new String(byteMP3V1,  3, 30, "Shift_JIS")).trim();
			Artist_  = (new String(byteMP3V1, 33, 30, "Shift-JIS")).trim();
			Album_   = (new String(byteMP3V1, 63, 30, "Shift-JIS")).trim();
			Year_    = (new String(byteMP3V1, 93,  4, "Shift-JIS")).trim();
			Comment_ = (new String(byteMP3V1, 97, 28, "Shift-JIS"));
			if(byteMP3V1[125] == 0x00) {
				TagVer_ = "1.1";
				TrackNo_ = (long)byteMP3V1[126];
			} else {
				TagVer_ = "1.0";
				Comment_ += (new String(byteMP3V1, 125, 2, "Shift-JIS"));
				Comment_.trim();
			}
			Genre_ = (long)(byteMP3V1[127] & 0xff);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private byte[] readFileMP3V1() {
		byte byteMP3V1[];

		if( objFile_ == null ) {
			System.out.print("ID3V1Info::readFileMP3V1() ERROR : objFile is Null!\n");
			return null;
		}

		byteMP3V1 = readFile(objFile_, (int)(objFile_.length()-128), 128);
		if( byteMP3V1 == null ) {
			System.out.print("ID3V1Info::readFileMP3V1() " + objFile_.getName() + "\n");
//				e.printStackTrace();
			return null;
		}
		return byteMP3V1;
	}
}

