/**
 * 타이머 관련 맵에 대한 컨트롤러
 * 2008. 12. 04
 */

package l1j.server.server;

import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.templates.TimeMap;

import java.util.ArrayList;

public class TimeMapController extends Thread {

    private static TimeMapController instance;                                        // 단일 싱글톤 객체
    private ArrayList<TimeMap> mapList;                                                // 맵 저장소

    /**
     * 기본생성자(싱글톤 구현으로 private)
     */
    private TimeMapController() {
        super("TimeMapController");
        mapList = new ArrayList<TimeMap>();
    }

    /**
     * 싱글톤 구현 - 단일 객체 리턴
     *
     * @return (TimeMapController)	단일객체
     */
    public static TimeMapController getInstance() {
        if (instance == null) instance = new TimeMapController();
        return instance;
    }

    /**
     * Thread abstract Method
     */
    @Override
    public void run() {
        try {
            while (true) {
                for (TimeMap timeMap : array()) {
                    if (timeMap.count()) {
                        for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
                            if (c.getMap().getId() == timeMap.getId())
                                L1Teleport.teleport(c, 32658, 32853, (short) 450, 5, true);
                        }
                        DoorSpawnTable.getInstance().getDoor(timeMap.getDoor()).close();
                        remove(timeMap);
                    }
                }
                Thread.sleep(1000L);
            }
        } catch (Exception e) {
            TimeMapController sTemp = new TimeMapController();
            copy(arrayList(), sTemp.arrayList());
            clear();
            sTemp.start();
            e.printStackTrace();
        }
    }

    /**
     * 타임 이벤트가 있는 맵 등록
     * 중복 등록이 되지 않도록 이미 등록된 맵 아이디와 비교 없다면 등록
     * 사이즈가 0 이라면 즉 초기라면 비교대상이 없기때문에 무조건 등록
     *
     * @param (TimeMap) 등록할 맵 객체
     */
    public void add(TimeMap map) {
        if (mapList.size() > 0) {
            for (TimeMap m : array()) {
                if (m.getId() != map.getId()) {
                    mapList.add(map);
                    break;
                }
            }
        } else mapList.add(map);
    }

    /**
     * 타임 이벤트가 있는 맵 삭제
     * 중복 삭제 또는 IndexOutOfBoundsException이 되지 않도록 이미 등록된 맵 아이디와 비교 있다면 삭제
     *
     * @param (TimeMap) 삭제할 맵 객체
     */
    private void remove(TimeMap map) {
        for (TimeMap m : array()) {
            if (m.getId() == map.getId()) {
                mapList.remove(map);
                break;
            }
        }
        map = null;
    }

    /**
     * 컨트롤러 리스트 초기화
     * 게임서버 종료시 요청(가급적으로 사용중지)
     */
    private void clear() {
        mapList.clear();
    }

    /**
     * 등록된 이벤트 맵 배열 리턴
     *
     * @return (TimeMap[])	맵 객체 배열
     */
    private TimeMap[] array() {
        return mapList.toArray(new TimeMap[mapList.size()]);
    }

    /**
     * 컨트롤러 리스트 객체(Exception 오류시 복사용)
     *
     * @return (ArrayList<TimeMap>)	맵 저장 리스트
     */
    private ArrayList<TimeMap> arrayList() {
        return mapList;
    }

    /**
     * 컨트롤러 예외 처리시 등록된 맵 이벤트를 유지시키기 위해 리스트 객체 복사
     * 향상된 for 문을 이용하되 예외 발생시 기존 for 문을 이용하여 복사
     *
     * @param (ArrayList<TimeMap>) src		원본 리스트
     * @param (ArrayList<TimeMap>) desc	복사될 리스트
     */
    private void copy(ArrayList<TimeMap> src, ArrayList<TimeMap> desc) {
        try {
            for (TimeMap map : src.toArray(new TimeMap[mapList.size()])) {
                if (!desc.contains(map)) desc.add(map);
            }
        } catch (Exception e) {
            for (int i = 0; i < src.size(); i++) {
                TimeMap map = src.get(i);
                if (!desc.contains(map)) desc.add(map);
            }
        }
    }
}