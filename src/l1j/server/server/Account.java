/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.Base64;
import l1j.server.Config; // IP당 계정 생성 개수 외부화
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

/**
 * 로그인을 위한 여러가지 인터페이스를 제공한다.
 */
public class Account {
	/** 어카운트명. */
	private String _name;

	/** 접속처의 IP주소. */
	private String _ip;

	/** 패스워드(암호화되고 있다). */
	private String _password;

	/** 최종 액티브일. */
	private Timestamp _lastActive;

	/** 액세스 레벨(GM인가? ). */
	private int _accessLevel;

	/** 접속처의 호스트명. */
	private String _host;

	/** 액세스 금지의 유무(True로 금지). */
	private boolean _banned;

	/** 어카운트가 유효한가 아닌가(True로 유효). */
	private boolean _isValid = false;

	/** 메세지 로그용. */
	private static Logger _log = Logger.getLogger(Account.class.getName());

	/**
	 * constructor　 　.
	 */
	private Account() {
	}

	/**
	 * 패스워드를 암호화한다.
	 *
	 * @param rawPassword
	 *            평문의 패스워드
	 * @return String
	 * @throws NoSuchAlgorithmException
	 *             암호 알고리즘을 사용할 수 없는 환경때
	 * @throws UnsupportedEncodingException
	 *             문자의 encode가 서포트되어 있지 않을 때
	 */
	private static String encodePassword(final String rawPassword)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] buf = rawPassword.getBytes("UTF-8");
		buf = MessageDigest.getInstance("SHA").digest(buf);

		return Base64.encodeBytes(buf);
	}

	/**
	 * 어카운트를 신규 작성한다.
	 *
	 * @param name
	 *            어카운트명
	 * @param rawPassword
	 *            평문패스워드
	 * @param ip
	 *            접속처의 IP주소
	 * @param host
	 *            접속처의 호스트명
	 * @return Account
	 */
	public static Account create(final String name, final String rawPassword,
			final String ip, final String host) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {

			Account account = new Account();
			account._name = name;
			account._password = encodePassword(rawPassword);
			account._ip = ip;
			account._host = host;
			account._banned = false;
			account._lastActive = new Timestamp(System.currentTimeMillis());

			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?,banned=? ";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, account._name);
			pstm.setString(2, account._password);
			pstm.setTimestamp(3, account._lastActive);
			pstm.setInt(4, 0);
			pstm.setString(5, account._ip);
			pstm.setString(6, account._host);
			pstm.setInt(7, account._banned ?  1 : 0);
			pstm.execute();
			_log.info("created new account for " + name);

			return account;
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (UnsupportedEncodingException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return null;
	}

	/**
	 * 어카운트 정보를 DB로부터 추출한다.
	 *
	 * @param name
	 *            어카운트명
	 * @return Account
	 */
	public static Account load(final String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		Account account = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT * FROM accounts WHERE login=?  LIMIT 1";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			if (!rs.next()) {
				return null;
			}
			account = new Account();
			account._name = rs.getString("login");
			account._password = rs.getString("password");
			account._lastActive = rs.getTimestamp("lastactive");
			account._accessLevel = rs.getInt("access_level");
			account._ip = rs.getString("ip");
			account._host = rs.getString("host");
			account._banned = rs.getInt("banned") == 0 ?  false : true;

			_log.fine("account exists");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return account;
	}

	/**
	 * 최종 로그인일을 DB에 반영한다.
	 *
	 * @param account
	 *            어카운트
	 */
	public static void updateLastActive(final Account account) {
		Connection con = null;
		PreparedStatement pstm = null;
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "UPDATE accounts SET lastactive=?  WHERE login = ? ";
			pstm = con.prepareStatement(sqlstr);
			pstm.setTimestamp(1, ts);
			pstm.setString(2, account.getName());
			pstm.execute();
			account._lastActive = ts;
			_log.fine("update lastactive for " + account.getName());
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 캐릭터 소유수를 카운트 한다.
	 *
	 * @return int
	 */
	public int countCharacters() {
		int result = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT count(*) as cnt FROM characters WHERE account_name=? ";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, _name);
			rs = pstm.executeQuery();
			if (rs.next()) {
				result = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	/**
	 * 어카운트를 무효로 한다.
	 *
	 * @param login
	 *            어카운트명
	 */
	public static void ban(final String login) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "UPDATE accounts SET banned=1 WHERE login=? ";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, login);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 입력된 패스워드와 DB상의 패스워드를 조합한다.
	 *
	 * @param rawPassword
	 *            평문패스워드
	 * @return boolean
	 */
	public boolean validatePassword(final String rawPassword) {
		// 인증 성공 후에 재차 인증되었을 경우는 실패시킨다.
		if (_isValid) {
			return false;
		}
		try {
			_isValid = _password.equals(encodePassword(rawPassword));
			if (_isValid) {
				_password = null; // 인증이 성공했을 경우, 패스워드를 파기한다.
			}
			return _isValid;
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 어카운트가 유효한가 어떤가를 돌려준다(True로 유효).
	 *
	 * @return boolean
	 */
	public boolean isValid() {
		return _isValid;
	}

	/**
	 * 어카운트가 게임 마스터인가 어떤가 돌려준다(True로 게임 마스터).
	 *
	 * @return boolean
	 */
	public boolean isGameMaster() {
		return 0 < _accessLevel;
	}

	/**
	 * 어카운트명을 취득한다.
	 *
	 * @return String
	 */
	public String getName() {
		return _name;
	}

	/**
	 * 접속처의 IP주소를 취득한다.
	 *
	 * @return String
	 */
	public String getIp() {
		return _ip;
	}

	/**
	 * 최종 로그인일을 취득한다.
	 */
	public Timestamp getLastActive() {
		return _lastActive;
	}

	/**
	 * 액세스 레벨을 취득한다.
	 *
	 * @return int
	 */
	public int getAccessLevel() {
		return _accessLevel;
	}

	/**
	 * 호스트명을 취득한다.
	 *
	 * @return String
	 */
	public String getHost() {
		return _host;
	}

	/**
	 * 액세스 금지 정보를 취득한다.
	 *
	 * @return boolean
	 */
	public boolean isBanned() {
		return _banned;
	}
// ########## A62 IP당 계정 생성 제한 
	public static boolean Check_LoginIP(String ip) {  
		int num = 0;  
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT count(ip) as cnt FROM accounts WHERE ip=?");
			pstm.setString(1, ip); 
			rs = pstm.executeQuery();
			if (rs.next()){
				num = rs.getInt("cnt");  
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
			if (num < Config.ACCOUNT_LIMIT){ 
				return false;
			}
			else{
				return true;
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		} return false;
	}
// ########## A62 IP당 계정 생성 제한 
}
