/**
 * ID3Tag�擾�v���O�����FMP3Info�N���X
 *  ID3Tag��Ver1.0/1.1��Ver2.3 �擾�N���X�̃X�[�p�[�N���X
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;


abstract class MP3Info {
	protected String strPath_ = "";
	protected String strFileName_ = "";
	static protected File objFile_ = null;

	/**
	 * �t�@�C�����:�p�X���擾
	 */
	public String getPath() {
		return objFile_.getPath();
	}

	/**
	 * �t�@�C�����:�t�@�C�������擾
	 */
	public String getFileName() {
		return objFile_.getName();
	}

	/**
	 * �t�@�C�����:�t�@�C���T�C�Y���擾
	 */
	public long getFileSize() {
		return objFile_.length();
	}

	/**
	 * �t�@�C�����:�ŏI�X�V�������擾
	 */
	public String getLastModified() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date(objFile_.lastModified()));
	}

	/**
	 * ID3:�^�C�g�����擾
	 */
	abstract public String getTitle();

	/**
	 * ID3:�A�[�e�B�X�g�����擾
	 */
	abstract public String getArtist();

	/**
	 * ID3:�A���o�������擾
	 */
	abstract public String getAlbum();

	/**
	 * ID3:�쐬�N���擾
	 */
	abstract public String getYear();

	/**
	 * ID3:�R�����g���擾
	 */
	abstract public String getComment();

	/**
	 * ID3:�g���b�N�ԍ����擾(v1.1�ȍ~)
	 */
	abstract public long getTrackNo();

	/**
	 * ID3:�W���������擾
	 */
	abstract public long getGenre();

	/**
	 * ID3:�^�O�o�[�W�������擾
	 */
	abstract public String getTagVersion();

	/**
	 * ID3���擾
	 */
	abstract public void getMP3Info(File objFile) throws UnsupportedEncodingException;

	/**
	 * �o�C�g�P�ʂŃt�@�C����ǂ�
	 * @param objFile   �ǂݍ��ݑΏۃt�@�C��
	 * @param iStartPos �ǂݍ��݊J�n�ʒu
	 * @param iLength   �ǂݍ��ݒ�
	 * @return �ǂݍ��񂾃f�[�^(byte�z��)
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


