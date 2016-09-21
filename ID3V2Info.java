/**
 * ID3Tag�擾�v���O�����FID3V2Info�N���X
 *  ID3Tag��Ver2.3 �擾�N���X
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
import java.util.Hashtable;
import mp3filelist.MP3Info;

	/**
	 * @note http://www1.cds.ne.jp/~takaaki/ID3/ID3v2.3.0J.html
	 */
public class ID3V2Info extends MP3Info {
	// MP3 V2_3

	private Hashtable ID3V2Info_ = null;
	private byte byteMP3V2_Header_[];

	private String Title_;
	private String Artist_;
	private String Album_;
	private String Year_;
	private String Comment_;
	private long TrackNo_;
	private long Genre_;

	public ID3V2Info(File objFile) throws UnsupportedEncodingException {
		Title_ = "";
		Artist_ = "";
		Album_ = "";
		Year_ = "";
		Comment_ = "";
		TrackNo_ = 0;
		Genre_ = 0;
		getMP3Info(objFile);
	}

	/**
	 * ID3:�^�C�g�����擾
	 */
	public String getTitle() {
		return Title_;
	}

	/**
	 * ID3:�A�[�e�B�X�g�����擾
	 */
	public String getArtist() {
		return Artist_;
	}

	/**
	 * ID3:�A���o�������擾
	 */
	public String getAlbum() {
		return Album_;
	}


	/**
	 * ID3:�쐬�N���擾
	 */
	public String getYear() {
		return Year_;
	}

	/**
	 * ID3:�R�����g���擾
	 */
	public String getComment() {
		return Comment_;
	}

	/**
	 * ID3:�g���b�N�ԍ����擾(v1.1�ȍ~)
	 */
	public long getTrackNo() {
		return TrackNo_;
	}


	/**
	 * ID3:�W�������ԍ����擾
	 */
	public long getGenre() {
		return Genre_;
	}

	/**
	 * ID3:�^�O�o�[�W�������擾
	 */
	public String getTagVersion() {
		return "2.3";
	}

	/**
	 * ID3V2������
	 * @note MP3 Tag V2 is Fast 1-3Byte data = ID3(0x49, 0x44, 0x33)
	 */
	static public boolean isSettingID3(File objFile) {
		byte byteMP3V2Tag[] = null;

		byteMP3V2Tag = readFile(objFile, 0, 3);
		if( byteMP3V2Tag != null ) {
			if((new String(byteMP3V2Tag, 0, 3)).matches("ID3")) {
				return true;
			}
		}
		return false;
	}

	/**
	ID3v2Header
	ID3 	: 3BYTE ID3 Tag v2 identifier "ID3"
	Version : 1BYTE 0x04
	Revision: 1BYTE 0x00
	Flags	: 1BYTE 
				7bit 1:asyncronous
				6bit 1:There is extend header 
				5bit 1:test 
				4bit 1:There is footer
				3-0bit : ALL ZERO
	Size	: 4BYTE(use MSB7bit)

	*/
	static public class ID3V2Header {
		static public long Version_;
		static public long Revision_;
		static public byte Flags_;
		static public long Size_;

		static public void clear() {
			Version_ = 0;
			Revision_ = 0;
			Flags_ = 0;
			Size_ = 0;
		}
	}

	/**
	 ID3v2 Expanded Header
	 Extended header size : 4BYTE $xx xx xx xx 
	 Extended Flags       : 2BYTE $xx xx
	 Size of padding      : 4BYTE $xx xx xx xx
	 CRC                  : 2BYTE %x0000000 00000000
	 */
	static public class ID3V2ExpandedHeader {
		
		static public long Size_;
		static public byte Flags_[] = null;
		static public long PaddingSize_;	// Padding��Frame�̌�ƃf�[�^�̑O�ɖ��߂�̈�
		static public byte CRC_[] = null;	// CRC��Flags��MSB��ON�̂Ƃ��ɂ���

		static public void clear() {
			Size_ = 0;
			Flags_ = null;
			PaddingSize_ = 0;
			CRC_ = null;
		}
	}

	/**
	 ID3v2 Frame
		FrameID   : 4BYTE(four characters)
		FrameSize : 4BYTE
		FLAG      : 2BYTE
		FrameData : FrameSize
	*/
	public class ID3V2Frame {
		public String FrameID_ = null;
		public long Size_;
		public byte Flag_[] = {0,0};
		public byte Data_[] = null;

		public void clear() {
			FrameID_ = null;
			Size_ = 0;
			Flag_ = null;
			Data_ = null;
		}
	}


	public void getMP3Info(File objFile) throws UnsupportedEncodingException {

		int iPos = 0;
		long lTotalFSize = 0;
		long lCurrentPos = 0;
		int iStartPos;

		if(!isSettingID3(objFile)) {
			return;
		}

		if( ID3V2Info_ == null) {
			ID3V2Info_ = new Hashtable();
		}

		ID3V2Header.clear();
		readFileMP3V2Header();
		// get ID3v2Header
		ID3V2Header.Version_ = (long)byteMP3V2_Header_[3];
		ID3V2Header.Revision_ = (long)byteMP3V2_Header_[4];
		ID3V2Header.Flags_ = byteMP3V2_Header_[5];

		ID3V2Header.Size_ = 0;
		for(iPos = 6; 10 > iPos; iPos++) {
			ID3V2Header.Size_ = ID3V2Header.Size_ * 0x80 + (byteMP3V2_Header_[iPos] & 0x7f);
		}
		iStartPos = 10;

		byte[] byteMP3V2_ExpandedHeader = null;
		ID3V2ExpandedHeader.clear();

		// there is extend header
		if((ID3V2Header.Flags_ & 0x40) != 0) {

			byteMP3V2_ExpandedHeader = readFile(objFile, 10, 4);

			if( byteMP3V2_ExpandedHeader == null ) {
				return;
			}
			ID3V2ExpandedHeader.Size_ = 0;
			for(iPos = 0; 4 > iPos; iPos++) {
				ID3V2ExpandedHeader.Size_ = ID3V2ExpandedHeader.Size_ * 0x100 + (byteMP3V2_ExpandedHeader[iPos]);
			}

			// ExpandFlag
			ID3V2ExpandedHeader.Flags_ = new byte[2];
			ID3V2ExpandedHeader.Flags_[0] = byteMP3V2_ExpandedHeader[4];
			ID3V2ExpandedHeader.Flags_[1] = byteMP3V2_ExpandedHeader[5];
			// (Flags_[0] && 0x80) == 0x80 : TRUE there is CRC

			ID3V2ExpandedHeader.PaddingSize_ = 0;
			for(iPos = 6; 10 > iPos; iPos++) {
				ID3V2ExpandedHeader.PaddingSize_ = ID3V2ExpandedHeader.PaddingSize_ * 0x100 + (byteMP3V2_ExpandedHeader[iPos]);
			}

			ID3V2ExpandedHeader.CRC_ = readFile(objFile, 10, 4);
			if( ID3V2ExpandedHeader.CRC_ == null ) {
				System.out.print("ID3V2Info::getMP3V2() read CRC" + objFile.getName() + "\n");
//					e.printStackTrace();
				return;
			}

			if( (ID3V2ExpandedHeader.CRC_[0] & 0x80) == 0x80 ) {
				// THERE IS CRC( IN EXPANDED HEDER)
				lTotalFSize = ID3V2Header.Size_ - 10;
				iStartPos += 14;
			} else {
				lTotalFSize = ID3V2Header.Size_ -  6;
				iStartPos += 10;
			}
		}

		byte byteMP3V2_Frame[] = null;
		lTotalFSize = ID3V2Header.Size_ - ID3V2ExpandedHeader.PaddingSize_ - 10;


		for(lCurrentPos = 0; lTotalFSize >= lCurrentPos; ) {
			byteMP3V2_Frame = readFile(objFile, iStartPos, 10);
			if( byteMP3V2_Frame != null ) {
				ID3V2Frame objID3V2Frame = new ID3V2Frame();

				objID3V2Frame.FrameID_ = new String(byteMP3V2_Frame, 0, 4);
				long dataSize = 0;
				for(iPos = 4; 8 > iPos; iPos++) {
					dataSize = dataSize * 0x100 + (long)(byteMP3V2_Frame[iPos] & 0xff);
				}
				objID3V2Frame.Size_ = dataSize;

				objID3V2Frame.Flag_ = new byte[2];
				objID3V2Frame.Flag_[0] = byteMP3V2_Frame[8];
				objID3V2Frame.Flag_[1] = byteMP3V2_Frame[9];

				lCurrentPos += 10;
				iStartPos += 10;
				objID3V2Frame.Data_ = readFile(objFile, iStartPos, (int)objID3V2Frame.Size_);
				if( objID3V2Frame.FrameID_.trim() != "" &&
					objID3V2Frame.Data_ != null ) {
					ID3V2Info_.put(objID3V2Frame.FrameID_, objID3V2Frame);
				} else {
					// TODO:ERROR
				}
				lCurrentPos += (int)objID3V2Frame.Size_;
				iStartPos += (int)objID3V2Frame.Size_;
			} else {
				break;
			}
		}

		Title_ = getTitle_();
		Artist_ = getArtist_();
		Album_ = getAlbum_();
		Year_ = getYear_();
		Comment_ = getComment_();
		TrackNo_ = getTrackNo_();
		Genre_ = getGenre_();

	}

	private boolean readFileMP3V2Header() {
		if( objFile_ == null ) {
			return false;
		}

		byteMP3V2_Header_ = readFile(objFile_, 0, 10);
		if( byteMP3V2_Header_ == null ) {
//				System.out.print("MP3Info::readFileMP3V2() " + objFile.getName() + "\n");
//				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ID3:�^�C�g�����擾
	 */
	private String getTitle_() throws UnsupportedEncodingException {
		ID3V2Frame objID3V2;
		objID3V2 = ((ID3V2Frame)ID3V2Info_.get("TIT2"));
		if(objID3V2 != null) {
			return (new String(objID3V2.Data_,  0, (int)objID3V2.Size_, "Shift_JIS")).trim();
		} else {
			return "";
		}
	}

	/**
	 * ID3:�A�[�e�B�X�g�����擾
	 */
	private String getArtist_() throws UnsupportedEncodingException {
		ID3V2Frame objID3V2;
		objID3V2 = ((ID3V2Frame)ID3V2Info_.get("TPE1"));
		if(objID3V2 != null) {
			return (new String(objID3V2.Data_,  0, (int)objID3V2.Size_, "Shift_JIS")).trim();
		} else {
			return "";
		}
	}

	/**
	 * ID3:�A���o�������擾
	 */
	private String getAlbum_() throws UnsupportedEncodingException {
		ID3V2Frame objID3V2;
		objID3V2 = ((ID3V2Frame)ID3V2Info_.get("TALB"));
		if(objID3V2 != null) {
			return (new String(objID3V2.Data_,  0, (int)objID3V2.Size_, "Shift_JIS")).trim();
		} else {
			return "";
		}
	}


	/**
	 * ID3:�쐬�N���擾
	 */
	private String getYear_() throws UnsupportedEncodingException {
		ID3V2Frame objID3V2;
		objID3V2 = ((ID3V2Frame)ID3V2Info_.get("TYER"));
		if(objID3V2 != null) {
			return (new String(objID3V2.Data_,  0, (int)objID3V2.Size_, "Shift_JIS")).trim();
		} else {
			return "";
		}
	}

	/**
	 * ID3:�R�����g���擾
	 */
	private String getComment_() throws UnsupportedEncodingException {
		ID3V2Frame objID3V2;
		objID3V2 = ((ID3V2Frame)ID3V2Info_.get("COMM"));
		if(objID3V2 != null) {
			return (new String(objID3V2.Data_,  0, (int)objID3V2.Size_, "Shift_JIS")).trim();
		} else {
			return "";
		}
	}

	/**
	 * ID3:�g���b�N�ԍ����擾(v1.1�ȍ~)
	 */
	private long getTrackNo_() {

		int i;
		int iState = 0;
		long lTrackNo = 0;
		ID3V2Frame objID3V2;
		boolean bIsGetTrackNo = false;

		objID3V2 = ((ID3V2Frame)ID3V2Info_.get("TRCK"));
		if(objID3V2 == null) {
			return -1;
		}

		// �������擾����B�����ȊO�̕������o����I��
		for(i=0; i > objID3V2.Size_; i++) {
			switch(iState) {
			case 0: // ���������擾
				if( Character.isDigit( (char)objID3V2.Data_[i] ) ) {
					lTrackNo = lTrackNo * 10 + Long.parseLong(new String(objID3V2.Data_, i, 1));
					bIsGetTrackNo = true;
				} else {
					iState = 1; // �����ȊO�̕������o���Ƃ��A�����̎擾�I��
				}
				break;
			}
			if(iState == 1) {
				break;
			}
		}

		// TrackNo���ݒ肳��Ă��Ȃ��ꍇ��-1��Ԃ��B
		if(!bIsGetTrackNo) {
			lTrackNo = -1;
		}

		return lTrackNo;
	}


	/**
	 * ID3:�W�������ԍ����擾
	 */
	private long getGenre_() {

		int i;
		int iState = 0;
		long lGenre = 0;
		ID3V2Frame objID3V2;
		boolean bIsGetGenre = false;

		objID3V2 = ((ID3V2Frame)ID3V2Info_.get("TCON"));
		if(objID3V2 == null) {
			return -1;
		}

		// �͂��߂ɏo�Ă���"("��")"�̊Ԃ̐��l��Ԃ��B���l����Ȃ��Ƃ���-1��Ԃ��B
		for(i=0; objID3V2.Size_ > i; i++) {
			switch(iState) {
			case 0:
				if((new String(objID3V2.Data_, i, 1)).equals("(") ) {
					iState = 1; // "("����
				}
				break;

			case 1: // "("���̐��������擾
				if( Character.isDigit( (char)objID3V2.Data_[i] ) ) {
					lGenre = lGenre * 10 + Long.parseLong(new String(objID3V2.Data_, i, 1));
					bIsGetGenre = true;
				} else {
					iState = 2; // "("�ȍ~�ɐ����ȊO�̕������o���Ƃ��A�����̎擾�I��
				}
				break;
			}
			if(iState == 2) {
				break;
			}
		}

		// �W���������ݒ肳��Ă��Ȃ��ꍇ��-1��Ԃ��B
		if(!bIsGetGenre) {
			lGenre = -1;
		}

		return lGenre;
	}

}


// ID3 v2.3 �^�OID�̈Ӗ�
//  TODO:v1�ő��݂�����̂̓����o�ϐ��ɕ��荞�ށBv1
//      ���Ƃ̓��X�g�ɓ˂����ށBID�ƒl�̑΂ɂȂ������̂�����
/*
AENC	�I�[�f�B�I�̈Í���
APIC	�t������摜
COMM	�R�����g
COMR	�R�}�[�V�����t���[��
ENCR	�Í����̎�@�̓o�^
EQUA	�ψꉻ
ETCO	�C�x���g�^�C���R�[�h
GEOB	�p�b�P�[�W�����ꂽ��ʓI�ȃI�u�W�F�N�g
GRID	�O���[�v���ʎq�̓o�^
IPLS	���͎�
LINK	�����N���
MCDI	���y�b�c���ʎq
MLLT	MPEG ���P�[�V�������b�N�A�b�v�e�[�u��
OWNE	���L���t���[��
PRIV	�v���C�x�[�g�t���[���v���C�x�[�g�t���[��
PCNT	���t��
POPM	�l�C���[�^�[
POSS	�����ʒu�t���[��
RBUF	�������߃o�b�t�@�T�C�Y
RVAD	���ΓI�{�����[������
RVRB	���o�[�u
SYLT	���� �̎�/����
SYTC	���� �e���|�R�[�h
TALB	�A���o��/�f��/�V���[�̃^�C�g��
TBPM	BPM (beats per minute�F�ꕪ�Ԃ̔���)
TCOM	��Ȏ�
TCON	���e�̃^�C�v
TCOP	���쌠���
TDAT	���t
TDLY	�v���C���X�g�x������
TENC	�G���R�[�h�����l
TEXT	�쎌��/�����쐬��
TFLT	�t�@�C���^�C�v
TIME	����
TIT1	���e�̑�����O���[�v�̐���
TIT2	�^�C�g��/�Ȗ�/���e�̐���
TIT3	�T�u�^�C�g��/�����̒ǉ����
TKEY	���߂̒�
TLAN	����
TLEN	����
TMED	���f�B�A�^�C�v
TOAL	�I���W�i���̃A���o��/�f��/�V���[�̃^�C�g��
TOFN	�I���W�i���t�@�C����
TOLY	�I���W�i���̍쎌��/�����쐬��
TOPE	�I���W�i���A�[�e�B�X�g/���t��
TORY	�I���W�i���̃����[�X�N
TOWN	�t�@�C���̏��L��/���C�Z���V�[
TPE1	��ȉ��t��/�\���X�g
TPE2	�o���h/�I�[�P�X�g��/���t
TPE3	�w����/���t�ҏڍ׏��
TPE4	�|���, ���~�b�N�X, ���̑��̏C��
TPOS	�Z�b�g���̈ʒu
TPUB	�o�Ŏ�
TRCK	�g���b�N�̔ԍ�/�Z�b�g���̈ʒu
TRDA	�^�����t
TRSN	�C���^�[�l�b�g���W�I�ǂ̖��O
TRSO	�C���^�[�l�b�g���W�I�ǂ̏��L��
TSIZ	�T�C�Y
TSRC	ISRC (international standard recording code�F���ەW�����R�[�f�B���O�R�[�h)
TSSE	�G���R�[�h�Ɏg�p�����\�t�g�E�G�A/�n�[�h�E�G�A�ƃZ�b�e�B���O
TYER	�N
TXXX	���[�U�[��`�������t���[��
UFID	��ӓI�ȃt�@�C�����ʎq
USER	�g�p����
USLT	�񓯊� �̎�/�����̃R�s�[
WCOM	���Ə�̏��
WCOP	���쌠/�@�I���
WOAF	�I�[�f�B�I�t�@�C���̌���Web�y�[�W
WOAR	�A�[�e�B�X�g/���t�҂̌���Web�y�[�W
WOAS	�����̌���Web�y�[�W
WORS	�C���^�[�l�b�g���W�I�ǂ̌����z�[���y�[�W
WPAY	�x����
WPUB	�o�ŎЂ̌���Web�y�[�W
WXXX	���[�U�[��`URL�����N�t���[��
*/



