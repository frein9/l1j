
package l1j.server.server;

import java.util.ArrayList;

import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.Instance.L1NpcInstance;

/**
 * 메세지 관련
 * @author user
 *
 */
public class MessageController extends Thread{
	
	// 리스트
	volatile private ArrayList<L1NpcInstance> list;
	
	// 싱글톤
	volatile private static MessageController _instance;
	
	/**
	 * Messagecontroller 객체 리턴
	 * @return 객체
	 */
	public static MessageController getInstance(){
		if(_instance == null) _instance = new MessageController();
		return _instance;
	}
	
	@Override
	public void run(){
		try{
			while(true){
				for(L1NpcInstance npc : toArray()){
					L1ShopItem s = ShopTable.getInstance().getShop(npc.getNpcTemplate().get_npcId());
					String[] sData = s.getMessage();
					if(sData != null){
						int idx = (int) (Math.random() * sData.length);
						npc.broadcastPacket(new S_DoActionShop(npc.getId(), ActionCodes.ACTION_Shop, sData[idx].getBytes()));
					}
				}
				sleep(5000L);  // for test 5sec
			}
		}catch(Exception e){
			e.printStackTrace();
			// 예외처리 ...
			// 등록된 리스트를  복사후 ... 재 시작
			MessageController desc = new MessageController();
			copyOf(desc, this);
			_instance = desc;
		}
	}
	// 비공개모드
	private MessageController(){
		super("MessageController Thread");
		list = new ArrayList<L1NpcInstance>();
	}
	
	/**
	 * npc 등록
	 * @param npc
	 */
	public void add(L1NpcInstance npc){
		synchronized(list){
			if(!list.contains(npc)){
				list.add(npc);
			}
		}
	}
	/**
	 * npc 삭제
	 * @param npc
	 */
	public void remove(L1NpcInstance npc){
		synchronized(list){
			if(list.contains(npc)) list.remove(npc);
		}
	}
	/**
	 * 리스트 반환
	 * @return
	 */
	public ArrayList<L1NpcInstance> getList(){
		return list;
	}
	/**
	 * npc 리스트 배열 
	 * @return
	 */
	public L1NpcInstance[] toArray(){
		return list.toArray(new L1NpcInstance[list.size()]);
	}
	
	private void copyOf(MessageController desc, MessageController src){
		try{
			for(L1NpcInstance n : src.toArray()) desc.add(n);
		}catch(Exception e){
			for(int i = 0; i < src.toArray().length; i++){
				desc.add(src.toArray()[i]);
			}
		}
	}
}
