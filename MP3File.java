/**
 * ID3Tag取得プログラム：MP3Fileクラス
 *  ID3TagのVer1.0/1.1とVer2.3 取得クラスを取得・保持するクラス
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
import java.util.*;
import mp3filelist.ID3V1Info;
import mp3filelist.ID3V2Info;


public class MP3File {

	private File objFile_;
	private ArrayList objMP3InfoList_;

	public MP3File(File objFile) {
		objFile_ = objFile;
		objMP3InfoList_ = null;
	}

	public ArrayList getMP3Info() throws UnsupportedEncodingException{

		objMP3InfoList_ = new ArrayList();
		if( ID3V1Info.isSettingID3(objFile_) ) {
			objMP3InfoList_.add(new ID3V1Info(objFile_));
		}
		if( ID3V2Info.isSettingID3(objFile_) ) {
			objMP3InfoList_.add(new ID3V2Info(objFile_));
		}
		return objMP3InfoList_;
	}

}
