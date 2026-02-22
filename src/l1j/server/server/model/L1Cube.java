
package l1j.server.server.model;

import java.util.ArrayList;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;

/**
 * 환술사 큐브 클래스
*/
public class L1Cube{

	/** 큐브 리스트 */
	private ArrayList<L1NpcInstance> CUBE[] = new ArrayList[4];

	/** 단일 클래스 */
	private static L1Cube instance;

	/** 인스턴스 초기화 */
	{
		for(int i = 0; i < CUBE.length; i++) CUBE[i] = new ArrayList<L1NpcInstance>();
	}

	/**
	 * 큐브 클래스 반환
	 * @return	단일 클래스 객체
	*/
	public static L1Cube getInstance(){
		if(instance == null) instance = new L1Cube();
		return instance;
	}

	/**
	 * 큐브 리스트 반납
	 * @param	index	리스트 인덱스
	*/
	private L1NpcInstance[] toArray(int index){
		return CUBE[index].toArray(new L1NpcInstance[CUBE[index].size()]);
	}
	/**
	 * 큐브 리스트 등록
	 * @param	index	리스트 인덱스
	 * @param	npc		등록될 npc 객체
	*/
	public void add(int index, L1NpcInstance npc){
		if(!CUBE[index].contains(npc)){
			CUBE[index].add(npc);
		}
	}
	/**
	 * 큐브 리스트 삭제
	 * @param	index	리스트 인덱스
	 * @param	npc		삭제될 npc 객체
	*/
	private void remove(int index, L1NpcInstance npc){
		if(CUBE[index].contains(npc)){
			CUBE[index].remove(npc);
		}
	}

	/** 비공개 */
	private L1Cube(){
		new CUBE1().start();
		new CUBE2().start();
		new CUBE3().start();
		new CUBE4().start();
	}

	/** 1단계 */
	class CUBE1 extends Thread{
		@Override
		public void run(){
			try{
				while(true){
					sleep(1000L);
					for(L1NpcInstance npc : toArray(0)){
						// 지속시간이 끝났다면
						if(npc.Cube()){
							npc.setCubePc(null);
							remove(0, npc);
							npc.deleteMe();
							continue;
						}
						if(npc.isCube()){
							// 주위 3셀 Pc 검색
							// 큐브를 뽑은 사람의 혈 우리편
							// 일단 다른혈은 적혈
							ArrayList<L1PcInstance> sTemp = L1World.getInstance().getVisiblePlayer(npc, 3);
							for(L1PcInstance c : sTemp.toArray(new L1PcInstance[sTemp.size()])){
								// 큐브에 있는 사람이 시전자이거나 같은 혈맹이라면
								if(npc.CubePc().getId() == c.getId() || npc.CubePc().getClanid() == c.getClanid()){
									if(!c.hasSkillEffect(5003)){
										c.addFire((byte) 30);
										// 여부 등록
										c.setSkillEffect(5003, 10 * 1000);
										c.sendPackets(new S_OwnCharAttrDef(c));
										c.sendPackets(new S_SkillSound(c.getId(), 6708));
									}
								}else c.receiveDamage(npc.CubePc(), 15);
							}
							npc.setCubeTime(4);
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/** 2단계 */
	class CUBE2 extends Thread{
		@Override
		public void run(){
			try{
				while(true){
					sleep(1000L);
					for(L1NpcInstance npc : toArray(1)){
						// 지속시간이 끝났다면
						if(npc.Cube()){
							npc.setCubePc(null);
							remove(1, npc);
							npc.deleteMe();
							continue;
						}
						if(npc.isCube()){
							// 주위 3셀 Pc 검색
							// 큐브를 뽑은 사람의 혈 우리편
							// 일단 다른혈은 적혈
							ArrayList<L1PcInstance> sTemp = L1World.getInstance().getVisiblePlayer(npc, 3);
							for(L1PcInstance c : sTemp.toArray(new L1PcInstance[sTemp.size()])){
								// 큐브에 있는 사람이 시전자이거나 같은 혈맹이라면
								if(npc.CubePc().getId() == c.getId() || npc.CubePc().getClanid() == c.getClanid()){
									if(!c.hasSkillEffect(5001)){
										c.addEarth((byte) 30);
										// 여부 등록
										c.setSkillEffect(5001, 10 * 1000);
										c.sendPackets(new S_OwnCharAttrDef(c));
										c.sendPackets(new S_SkillSound(c.getId(), 6714));
										
										// for test
										//System.out.println("큐브 당함  " + c.getEarth());
									}
								}else{
									c.sendPackets(new S_Poison(c.getId(), 2));
									c.broadcastPacket(new S_Poison(c.getId(), 2));
									c.sendPackets(new S_Paralysis(4, true));
									c.setSkillEffect(157, 1 * 1000);
								}
							}
							npc.setCubeTime(4);
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/** 3단계 */
	class CUBE3 extends Thread{
		@Override
		public void run(){
			try{
				while(true){
					sleep(1000L);
					for(L1NpcInstance npc : toArray(2)){
						// 지속시간이 끝났다면
						if(npc.Cube()){
							npc.setCubePc(null);
							remove(2, npc);
							npc.deleteMe();
							continue;
						}
						if(npc.isCube()){
							// 주위 3셀 Pc 검색
							// 큐브를 뽑은 사람의 혈 우리편
							// 일단 다른혈은 적혈
							ArrayList<L1PcInstance> sTemp = L1World.getInstance().getVisiblePlayer(npc, 3);
							for(L1PcInstance c : sTemp.toArray(new L1PcInstance[sTemp.size()])){
								// 큐브에 있는 사람이 시전자이거나 같은 혈맹이라면
								if(npc.CubePc().getId() == c.getId() || npc.CubePc().getClanid() == c.getClanid()){
									if(!c.hasSkillEffect(5002)){
										c.addWind((byte) 30);
										// 여부 등록
										c.setSkillEffect(5002, 10 * 1000);
										c.sendPackets(new S_OwnCharAttrDef(c));
										c.sendPackets(new S_SkillSound(c.getId(), 6720));
									}
								}else{
									if(!c.hasSkillEffect(7979)){
										c.setSkillEffect(7979, 16 * 1000);
									}
									int getMr = 0;
									getMr = (int) (25 * c.getTrueMr()) / 100;
									c.addMr(-getMr);
									c.CubeMr += getMr;
									c.sendPackets(new S_SPMR(c));
								}
							}
							npc.setCubeTime(4);
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/** 4단계 */
	class CUBE4 extends Thread{
		@Override
		public void run(){
			try{
				while(true){
					sleep(1000L);
					for(L1NpcInstance npc : toArray(3)){
						// 지속시간이 끝났다면
						if(npc.Cube()){
							npc.setCubePc(null);
							remove(3, npc);
							npc.deleteMe();
							continue;
						}
						if(npc.isCube()){
							// 주위 3셀 Pc 검색
							// 큐브를 뽑은 사람의 혈 우리편
							// 일단 다른혈은 적혈
							ArrayList<L1PcInstance> sTemp = L1World.getInstance().getVisiblePlayer(npc, 3);
							for(L1PcInstance c : sTemp.toArray(new L1PcInstance[sTemp.size()])){
								if(c != null){
									if(c.getCurrentHp() > 0){
										c.receiveDamage(npc.CubePc(), 25);
										c.setCurrentMp(c.getCurrentMp() + 5);
									}
								}
							}
							npc.setCubeTime(5);
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}