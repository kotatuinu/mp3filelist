/**
 * ID3Tag�擾�v���O�����FMP3File�N���X
 *  ID3Tag��Ver1.0/1.1��Ver2.3 �擾�N���X���擾�E�ێ�����N���X
 *
 * Copyleft 2008, Kotatuinu.
 *
 * title   : ID3Tag Getter
 * author  : �x����
 * version : 0.1
 * mail    : kotatuinu@nifty.com
 * Website : http://homepage2.nifty.com/kotatuinu/
 * Released: 2008/03/09
 * NOTICE  : 
 * �{�v���O�����́A���p���p����щ��������R�ɍs���Ă��������Ă����܂��܂���B
 * �������Ajava�̍X�Ȃ锭�W�̂��߁A���������\�[�X�͌��J���Ă��������B
 * ���p�E�����̘A���͕s�v�ł��B
 * �Ȃ��A�{�v���O�����̗��p�ɂ�肠�Ȃ��A�܂��͂��Ȃ��̎��͂ɑ��Q���������Ă��A
 * �����͈�؊֒m���܂���B
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
