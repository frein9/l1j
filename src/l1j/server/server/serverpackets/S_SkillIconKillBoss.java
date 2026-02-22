package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

//0000 : 6c 16 dd fc 03 01 61 ba                            l.....a.

public class S_SkillIconKillBoss extends ServerBasePacket{

 public S_SkillIconKillBoss(int type, int time){
  writeC(Opcodes.S_OPCODE_SKILLICONGFX);
  writeC(0x16);
  writeC(0xdd);
  writeH(time); // 기본시간은 1020초
  writeC(type); // 1: 발록, 2: 야히
 }

 @Override
 public byte[] getContent(){
  return getBytes();
 }
}

