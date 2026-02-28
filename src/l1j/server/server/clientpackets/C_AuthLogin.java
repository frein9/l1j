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
package l1j.server.server.clientpackets;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Account;
import l1j.server.server.AccountAlreadyLoginException;
import l1j.server.server.ClientThread;
import l1j.server.server.GameServerFullException;
import l1j.server.server.LoginController;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_CommonNews;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_LoginResult;
import l1j.server.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import l1j.server.server.clientpackets.C_CommonClick;//로그인부 구조변경을 위해 추가

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_AuthLogin extends ClientBasePacket {

    private static final String C_AUTH_LOGIN = "[C] C_AuthLogin";
    private static Logger _log = Logger.getLogger(C_AuthLogin.class.getName());

    public C_AuthLogin(byte[] decrypt, ClientThread client) {
        super(decrypt);
        String accountName = readS().toLowerCase();
        String password = readS();

        String ip = client.getIp();
        String host = client.getHostname();
        client.ipcountzero(ip); //## 아이피 카운트 제로선언


        _log.finest("Request AuthLogin from user : " + accountName);

        if (!Config.ALLOW_2PC) {
            for (ClientThread tempClient : LoginController.getInstance()
                    .getAllAccounts()) {
                if (ip.equalsIgnoreCase(tempClient.getIp())) {
                    _log.info("2 PC의 로그인을 거부했습니다.account="
                            + accountName + " host=" + host);
                    client.sendPacket(new S_LoginResult(
                            S_LoginResult.REASON_USER_OR_PASS_WRONG));
                    return;
                }
                if (accountName.equalsIgnoreCase(tempClient.getAccountName())) {
                    _log.warning("두 케릭터 버그 시도가 감지되었습니다. account=" + accountName);
                    tempClient.kick();
                    client.kick();
                    return;
                }
            }
        }

        Account account = Account.load(accountName);

        if (account == null) {
            if (Config.AUTO_CREATE_ACCOUNTS) {
                // ########## A105 계정명 길이 제한 및 공백으로 생성 못하게
                if (isDisitAlaha(accountName) == false) {
                    client.sendPacket(new S_LoginResult(
                            S_LoginResult.REASON_ACCESS_FAILED));
                    return;
                } else if (accountName.length() < 4 || accountName.length() > 12) {
                    client.sendPacket(new S_LoginResult(
                            S_LoginResult.REASON_ACCESS_FAILED));
                    return;
                }
                // ########## A105 계정명 길이 제한 및 공백으로 생성 못하게

                // ########## A62 IP당 계정 생성 제한
                if (Account.Check_LoginIP(ip)) {
                    _log.info("Connect from IP check : " + ip);
                    client.sendPacket(new S_CommonNews("이미 계정을 소유하시고 계십니다."));
                    try {
                        Thread.sleep(1500);
                        client.kick();
                    } catch (Exception e1) {
                    }
                    return;
                }
                // ########## A62 IP당 계정 생성 제한
                account = Account.create(accountName, password, ip, host);
            } else {
                _log.warning("account missing for user " + accountName);
            }
        }
        if (account == null || !account.validatePassword(password)) {
            client.sendPacket(new S_LoginResult(
                    S_LoginResult.REASON_USER_OR_PASS_WRONG));
            return;
        }
        if (account.isBanned()) { // BAN 어카운트
            _log.info("BAN 어카운트의 로그인을 거부했습니다.account=" + accountName + " host="
                    + host);
            client.sendPacket(new S_LoginResult(
                    S_LoginResult.REASON_USER_OR_PASS_WRONG));
            return;
        }
        if (checkLoadAccount(accountName) > 0) {
            _log.warning("두 캐릭터 버그 시도가 감지되었습니다. account=" + accountName);
            client.kick();
            return;
        }
        try {
            LoginController.getInstance().login(client, account);
            Account.updateLastActive(account); // 최종 로그인일을 갱신한다
            client.setAccount(account);
            //client.sendPacket(new S_LoginResult(S_LoginResult.REASON_LOGIN_OK)); // ########## A96 EPU 전환 위해 원본 소스 주석 처리 ##########
            client.sendPacket(new S_CommonNews());
        } catch (GameServerFullException e) {
            client.kick();
            _log.info("접속 인원수 상한에 이르고 있기 때문에(위해)(" + client.getHostname()
                    + ")의 로그인을 거부해, 절단 했습니다.");
            return;
        } catch (AccountAlreadyLoginException e) {
            client.kick();
            _log.info("동일 ID에서의 중복 접속 (위해)때문에(" + client.getHostname()
                    + ")(와)과의 접속을 강제 절단 했습니다.");
            return;
        }
    }

    // ########## A105 계정명 길이 제한 및 공백으로 생성 못하게
    private static boolean isDisitAlaha(String str) {
        boolean check = true;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)) // 숫자가 아니라면
                    && Character.isLetterOrDigit(str.charAt(i)) // 특수문자라면
                    && !Character.isUpperCase(str.charAt(i)) // 대문자가 아니라면
                    && Character.isWhitespace(str.charAt(i)) // 공백이라면
                    && !Character.isLowerCase(str.charAt(i))) { // 소문자가 아니라면
                check = false;
                break;
            }
        }
        return check;
    }

    // 두케릭 버그 방지용.
    /*
     * 기본적으로 로긴한 계정명으로 월드내에 월드내에 OnlineStatus 가 1 인 케릭터가 있는지를 찾아서
     * 있다면 해당 케릭터의 정보를 받아 Disconnect 시키고 갯수를 돌려줌.
     */
    private int checkLoadAccount(String account) {
        int resultFlag = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        L1PcInstance BugPc = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT CHAR_NAME FROM CHARACTERS WHERE ACCOUNT_NAME=?");
            pstm.setString(1, account);
            rs = pstm.executeQuery();

            while (rs.next()) {
                BugPc = L1World.getInstance().getPlayer(rs.getString(1));
                if (BugPc != null && BugPc.getOnlineStatus() == 1 && !BugPc.isPrivateShop()) {
                    ClientThread.quitGame(BugPc);
                    BugPc.sendPackets(new S_Disconnect());
                    resultFlag++;
                }
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            BugPc = null;
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return resultFlag;
    }

    @Override
    public String getType() {
        return C_AUTH_LOGIN;
    }

}