/**
 * ID3Tag取得プログラム：ID3V2Infoクラス
 *  ID3TagのVer2.3 取得クラス
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
		return "2.3";
	}

	/**
	 * ID3V2か判定
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
		static public long PaddingSize_;	// PaddingはFrameの後とデータの前に埋める領域
		static public byte CRC_[] = null;	// CRCはFlagsのMSBがONのときにある

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
	 * ID3:タイトルを取得
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
	 * ID3:アーティスト名を取得
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
	 * ID3:アルバム名を取得
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
	 * ID3:作成年を取得
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
	 * ID3:コメントを取得
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
	 * ID3:トラック番号を取得(v1.1以降)
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

		// 数字を取得する。数字以外の文字が出たら終了
		for(i=0; i > objID3V2.Size_; i++) {
			switch(iState) {
			case 0: // 数字文字取得
				if( Character.isDigit( (char)objID3V2.Data_[i] ) ) {
					lTrackNo = lTrackNo * 10 + Long.parseLong(new String(objID3V2.Data_, i, 1));
					bIsGetTrackNo = true;
				} else {
					iState = 1; // 数字以外の文字が出たとき、数字の取得終了
				}
				break;
			}
			if(iState == 1) {
				break;
			}
		}

		// TrackNoが設定されていない場合は-1を返す。
		if(!bIsGetTrackNo) {
			lTrackNo = -1;
		}

		return lTrackNo;
	}


	/**
	 * ID3:ジャンル番号を取得
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

		// はじめに出てきた"("と")"の間の数値を返す。数値じゃないときは-1を返す。
		for(i=0; objID3V2.Size_ > i; i++) {
			switch(iState) {
			case 0:
				if((new String(objID3V2.Data_, i, 1)).equals("(") ) {
					iState = 1; // "("発見
				}
				break;

			case 1: // "("内の数字文字取得
				if( Character.isDigit( (char)objID3V2.Data_[i] ) ) {
					lGenre = lGenre * 10 + Long.parseLong(new String(objID3V2.Data_, i, 1));
					bIsGetGenre = true;
				} else {
					iState = 2; // "("以降に数字以外の文字が出たとき、数字の取得終了
				}
				break;
			}
			if(iState == 2) {
				break;
			}
		}

		// ジャンルが設定されていない場合は-1を返す。
		if(!bIsGetGenre) {
			lGenre = -1;
		}

		return lGenre;
	}

}


// ID3 v2.3 タグIDの意味
//  TODO:v1で存在するものはメンバ変数に放り込む。v1
//      あとはリストに突っ込む。IDと値の対になったものを入れる
/*
AENC	オーディオの暗号化
APIC	付属する画像
COMM	コメント
COMR	コマーシャルフレーム
ENCR	暗号化の手法の登録
EQUA	均一化
ETCO	イベントタイムコード
GEOB	パッケージ化された一般的なオブジェクト
GRID	グループ識別子の登録
IPLS	協力者
LINK	リンク情報
MCDI	音楽ＣＤ識別子
MLLT	MPEG ロケーションルックアップテーブル
OWNE	所有権フレーム
PRIV	プライベートフレームプライベートフレーム
PCNT	演奏回数
POPM	人気メーター
POSS	同期位置フレーム
RBUF	おすすめバッファサイズ
RVAD	相対的ボリューム調整
RVRB	リバーブ
SYLT	同期 歌詞/文書
SYTC	同期 テンポコード
TALB	アルバム/映画/ショーのタイトル
TBPM	BPM (beats per minute：一分間の拍数)
TCOM	作曲者
TCON	内容のタイプ
TCOP	著作権情報
TDAT	日付
TDLY	プレイリスト遅延時間
TENC	エンコードした人
TEXT	作詞家/文書作成者
TFLT	ファイルタイプ
TIME	時間
TIT1	内容の属するグループの説明
TIT2	タイトル/曲名/内容の説明
TIT3	サブタイトル/説明の追加情報
TKEY	初めの調
TLAN	言語
TLEN	長さ
TMED	メディアタイプ
TOAL	オリジナルのアルバム/映画/ショーのタイトル
TOFN	オリジナルファイル名
TOLY	オリジナルの作詞家/文書作成者
TOPE	オリジナルアーティスト/演奏者
TORY	オリジナルのリリース年
TOWN	ファイルの所有者/ライセンシー
TPE1	主な演奏者/ソリスト
TPE2	バンド/オーケストラ/伴奏
TPE3	指揮者/演奏者詳細情報
TPE4	翻訳者, リミックス, その他の修正
TPOS	セット中の位置
TPUB	出版社
TRCK	トラックの番号/セット中の位置
TRDA	録音日付
TRSN	インターネットラジオ局の名前
TRSO	インターネットラジオ局の所有者
TSIZ	サイズ
TSRC	ISRC (international standard recording code：国際標準レコーディングコード)
TSSE	エンコードに使用したソフトウエア/ハードウエアとセッティング
TYER	年
TXXX	ユーザー定義文字情報フレーム
UFID	一意的なファイル識別子
USER	使用条件
USLT	非同期 歌詞/文書のコピー
WCOM	商業上の情報
WCOP	著作権/法的情報
WOAF	オーディオファイルの公式Webページ
WOAR	アーティスト/演奏者の公式Webページ
WOAS	音源の公式Webページ
WORS	インターネットラジオ局の公式ホームページ
WPAY	支払い
WPUB	出版社の公式Webページ
WXXX	ユーザー定義URLリンクフレーム
*/



