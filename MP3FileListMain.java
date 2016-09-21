/**
 * ID3Tag�擾�v���O�����FMP3FileListMain�N���X
 *  Main�N���X
 *
 * [Usage]
 * �R���\�[�����AMP3�̃^�O�ꗗ���擾�������f�B���N�g���������Ɏw�肷��B
 * >java mp3filelist/MP3FileListMain <�f�B���N�g��>
 *
 * �W���o�͂�ID3 tag�̓��e��csv���ۂ��`���ŏo�͂��܂��B
 * �o�͓��e��ύX�������ꍇ�́AoutputFiles���\�b�h���C�����Ă��������B
 * �Ȃ��Av1.0�܂���v1.1 �� v2.3�̗����Ƃ��ݒ肳��Ă���ꍇ�A���҂Ƃ��o�͂���悤�ɂȂ��Ă��܂��B
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
			System.out.print("�����Ƀf�B���N�g�����w�肵��\n");
			return;
		}
		File objFile = new File(args[0]);
		
		if(!objFile.isDirectory()) {
			System.out.print("�����Ɏw�肵���̂́A�f�B���N�g���ł͂Ȃ�\n");
			System.out.print("�����Ƀf�B���N�g�����w�肵��\n");
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

					MP3Info objMP3Info = (MP3Info)objMP3List.get(i);	// �b��I

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
