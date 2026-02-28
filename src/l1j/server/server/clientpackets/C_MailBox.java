package l1j.server.server.clientpackets;

import l1j.server.server.ClientThread;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_LetterList;
import l1j.server.server.serverpackets.S_ReadLetter;
import l1j.server.server.serverpackets.S_RenewLetter;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class C_MailBox extends ClientBasePacket {
    private static final String C_MailBox = "[C] C_MailBox";
    private static Logger _log = Logger.getLogger(C_MailBox.class.getName());

    public C_MailBox(byte abyte0[], ClientThread client) {
        super(abyte0);
        int type = readC();

        L1PcInstance pc = client.getActiveChar();
        //pc.sendPackets(new S_RenewLetter(pc,type,0));
        switch (type) {
            //메일함 종류에 따른 값
            case 0:    //개인메일함
                LetterList(pc, type, 20);
                break;
            case 1: //혈맹메일함
                LetterList(pc, type, 50);
                break;
            case 2: //보관함
                LetterList(pc, type, 10);
                break;
            case 16: //개인메일 읽기
                ReadLetter(pc, type, 0);
                break;
            case 17: //혈맹메일 읽기
                ReadLetter(pc, type, 1);
                break;
            case 18: //보관메일 읽기
                ReadLetter(pc, type, 2);
                break;
            case 32: //개인메일 쓰기
                WriteLetter(pc, 0, 50);
                break;
            case 33: //혈맹메일 쓰기
                WriteLetter(pc, 1, 1000);
                break;
            case 48:
                DeleteLetter(pc, type, 0);
                break;
            case 49:
                DeleteLetter(pc, type, 1);
                break;
            case 50:
                DeleteLetter(pc, type, 2);
                break;
            case 64:
                SaveLetter(pc, type, 2);
                break;
            default:

                //LetterList(pc,type);
        }
    }

    //편지를 쓰기위한 메소드
    // Params
    // @pc 케릭터
    // @type 메일함 형태
    // @price 가격
    private void WriteLetter(L1PcInstance pc, int type, int price) {

        int nu1 = readH(); //편지지
        SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd", Locale.KOREA);
        Date currentTime = new Date();
        String dTime = formatter.format(currentTime);

        String to = readS();
        String subject = readSS();
        String content = readSS();

        int AdenaCnt = pc.getInventory().countItems(L1ItemId.ADENA);
        if (AdenaCnt >= price) {
            pc.getInventory().consumeItem(L1ItemId.ADENA, price);
            if (type == 0) { //개인메일일 경우
                L1PcInstance target = L1World.getInstance().getPlayer(to);
                LetterTable.getInstance().writeLetter(0, nu1, dTime, pc.getName(), to, type, subject, content);
                if (target != null && target.getOnlineStatus() != 0) {
                    LetterList(target, type, 20);
                    target.sendPackets(new S_SkillSound(target.getId(), 1091));
                    target.sendPackets(new S_ServerMessage(428)); // 편지가 도착했습니다.
                    pc.sendPackets(new S_LetterList(pc, type, 20));
                }
            } else if (type == 1) { //혈맹 메일일경우
                L1Clan targetClan = null;
                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getClanName().toLowerCase().equals(to.toLowerCase())) {
                        targetClan = clan;
                        break;
                    }
                }
                String memberName[] = targetClan.getAllMembers();
                for (int i = 0; i < memberName.length; i++) {
                    L1PcInstance target = L1World.getInstance().getPlayer(memberName[i]);
                    LetterTable.getInstance().writeLetter(0, nu1, dTime, pc.getName(), memberName[i], type, subject, content);
                    if (target != null && target.getOnlineStatus() != 0) {
                        LetterList(target, type, 50);
                        target.sendPackets(new S_SkillSound(target.getId(), 1091));
                        target.sendPackets(new S_ServerMessage(428)); // 편지가 도착했습니다.
                        pc.sendPackets(new S_LetterList(pc, type, 50));
                    }
                }
            }
        } else {
            pc.sendPackets(new S_ServerMessage(189, ""));
        }
    }

    //편지를 삭제하기위한 메소드
    private void DeleteLetter(L1PcInstance pc, int type, int letterType) {
        int id = readD();
        LetterTable.getInstance().deleteLetter(id);
        pc.sendPackets(new S_RenewLetter(pc, type, id));
    }

    //편지를 읽기위한 메소드
    private void ReadLetter(L1PcInstance pc, int type, int letterType) {
        int id = readD();
        LetterTable.getInstance().CheckLetter(id);
        pc.sendPackets(new S_ReadLetter(pc, type, letterType, id));
    }

    //편지리스트 출력을위한 메소드
    private void LetterList(L1PcInstance pc, int type, int count) {
        pc.sendPackets(new S_LetterList(pc, type, count));
    }

    //편지를 보관하기 위함 메소드
    private void SaveLetter(L1PcInstance pc, int type, int letterType) {
        int id = readD();
        LetterTable.getInstance().SaveLetter(id, letterType);
        pc.sendPackets(new S_RenewLetter(pc, type, id));
    }

    @Override
    public String getType() {
        return C_MailBox;
    }
}
