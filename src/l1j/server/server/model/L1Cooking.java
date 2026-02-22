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
package l1j.server.server.model;

import java.util.logging.Logger;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SPMR;
import static l1j.server.server.model.skill.L1SkillId.*;

// Referenced classes of package l1j.server.server.model:
// L1Cooking

public class L1Cooking {
	private static final Logger _log = Logger.getLogger(L1Cooking.class
			.getName());

	private L1Cooking() {
	}

	public static void useCookingItem(L1PcInstance pc, L1ItemInstance item) {
		int itemId = item.getItem().getItemId();
		if (itemId == 41284 || itemId == 41292 
		|| itemId == 50018 || itemId == 50026
		|| itemId == 50035 || itemId == 50051) { // 디저트
			if (pc.get_food() != 225) {
				pc.sendPackets(new S_ServerMessage(74, item
						.getNumberedName(1))); // \f1%0은 사용할 수 없습니다.
				return;
			}
		}

		if (itemId >= 41277 && itemId <= 41283 // Lv1 요리
				|| itemId >= 41285 && itemId <= 41291 // Lv1 환상의 요리
				|| itemId >= 50011 && itemId <= 50017 // Lv2 요리
				|| itemId >= 50019 && itemId <= 50025 // Lv2 환상의 요리
				|| itemId >= 50036 && itemId <= 50042 // Lv3 요리
				|| itemId >= 50044 && itemId <= 50050) { // Lv3 환상의 요리
			int cookingId = pc.getCookingId();
			if (cookingId != 0) {
				pc.removeSkillEffect(cookingId);
				
			}
		}
		if (itemId == 41284 || itemId == 41292
			|| itemId == 50018 || itemId == 50026
			|| itemId == 50035 || itemId == 50051) { // 디저트
			int dessertId = pc.getDessertId();
			if (dessertId != 0) {
				pc.removeSkillEffect(dessertId);
			
			}
		}

		int cookingId;
		int time = 900;
		if (itemId == 41277 || itemId == 41285) { // 괴물눈 스테이크
			if (itemId == 41277) {
				cookingId = COOKING_1_0_N;
			} else {
				cookingId = COOKING_1_0_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 41278 || itemId == 41286) { // 곰고기 구이
			if (itemId == 41278) {
				cookingId = COOKING_1_1_N;
			} else {
				cookingId = COOKING_1_1_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 41279 || itemId == 41287) { // 씨호떡
			if (itemId == 41279) {
				cookingId = COOKING_1_2_N;
			} else {
				cookingId = COOKING_1_2_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 41280 || itemId == 41288) { // 개미다리 치즈구이
			if (itemId == 41280) {
				cookingId = COOKING_1_3_N;
			} else {
				cookingId = COOKING_1_3_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 41281 || itemId == 41289) { // 과일샐러드
			if (itemId == 41281) {
				cookingId = COOKING_1_4_N;
			} else {
				cookingId = COOKING_1_4_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 41282 || itemId == 41290) { // 과일탕수육
			if (itemId == 41282) {
				cookingId = COOKING_1_5_N;
			} else {
				cookingId = COOKING_1_5_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 41283 || itemId == 41291) { // 멧돼지 꼬치구이
			if (itemId == 41283) {
				cookingId = COOKING_1_6_N;
			} else {
				cookingId = COOKING_1_6_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 41284 || itemId == 41292) { // 버섯 스프
			if (itemId == 41284) {
				cookingId = COOKING_1_7_N;
			} else {
				cookingId = COOKING_1_7_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50011 || itemId == 50019) { // 캐비어 카나페
			if (itemId == 50011) {
				cookingId = COOKING_1_8_N;
			} else {
				cookingId = COOKING_1_8_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50012 || itemId == 50020) { // 악어 스테이크
			if (itemId == 50012) {
				cookingId = COOKING_1_9_N;
			} else {
				cookingId = COOKING_1_9_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50013 || itemId == 50021) { // 터틀 드래곤 껍질 과자
			if (itemId == 50013) {
				cookingId = COOKING_1_10_N;
			} else {
				cookingId = COOKING_1_10_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50014 || itemId == 50022) { // 키위 패롯 구이
			if (itemId == 50014) {
				cookingId = COOKING_1_11_N;
			} else {
				cookingId = COOKING_1_11_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50015 || itemId == 50023) { // 스콜피온  구이
			if (itemId == 50015) {
				cookingId = COOKING_1_12_N;
			} else {
				cookingId = COOKING_1_12_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50016 || itemId == 50024) { // 일렉카듐 스튜
			if (itemId == 50016) {
				cookingId = COOKING_1_13_N;
			} else {
				cookingId = COOKING_1_13_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50017 || itemId == 50025) { // 거미다리 꼬치구이
			if (itemId == 50017) {
				cookingId = COOKING_1_14_N;
			} else {
				cookingId = COOKING_1_14_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50018 || itemId == 50026) { // 크랩살 스프
			if (itemId == 50018) {
				cookingId = COOKING_1_15_N;
			} else {
				cookingId = COOKING_1_15_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50036 || itemId == 50044) { // 크러스트 시안 집게발 구이
			if (itemId == 50036) {
				cookingId = COOKING_1_16_N;
			} else {
				cookingId = COOKING_1_16_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50037 || itemId == 50045) { // 그리폰 구이
			if (itemId == 50037) {
				cookingId = COOKING_1_17_N;
			} else {
				cookingId = COOKING_1_17_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50038 || itemId == 50046) { // 코카트리스 스테이크
			if (itemId == 50038) {
				cookingId = COOKING_1_18_N;
			} else {
				cookingId = COOKING_1_18_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50039 || itemId == 50047) { // 대왕거북구이
			if (itemId == 50039) {
				cookingId = COOKING_1_19_N;
			} else {
				cookingId = COOKING_1_19_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50040 || itemId == 50048) { // 레서 드래곤 날개 꼬치
			if (itemId == 50040) {
				cookingId = COOKING_1_20_N;
			} else {
				cookingId = COOKING_1_20_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50041 || itemId == 50049) { // 드레이크 구이
			if (itemId == 50041) {
				cookingId = COOKING_1_21_N;
			} else {
				cookingId = COOKING_1_21_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50042 || itemId == 50050) { // 심해어 스튜
			if (itemId == 50042) {
				cookingId = COOKING_1_22_N;
			} else {
				cookingId = COOKING_1_22_S;
			}
			eatCooking(pc, cookingId, time);
		} else if (itemId == 50043 || itemId == 50051) { // 바실리스크 알 스프
			if (itemId == 50043) {
				cookingId = COOKING_1_23_N;
			} else {
				cookingId = COOKING_1_23_S;
			}
			eatCooking(pc, cookingId, time);
		} 
		pc.sendPackets(new S_ServerMessage(76, item.getNumberedName(1))); // \f1%0을 먹었습니다.
		pc.getInventory().removeItem(item , 1);
	}

	public static void eatCooking(L1PcInstance pc, int cookingId, int time) {
		int cookingType = 0;
		if (cookingId == COOKING_1_0_N || cookingId == COOKING_1_0_S) { //괴물 눈 스테이크
			cookingType = 0;
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		} else if (cookingId == COOKING_1_1_N || cookingId == COOKING_1_1_S) { //곰고기 구이
			cookingType = 1;
			pc.addMaxHp(30);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // 파티중
				pc.getParty().updateMiniHP(pc);
			}
		} else if (cookingId == COOKING_1_2_N || cookingId == COOKING_1_2_S) { //씨호떡
			cookingType = 2;
			pc.addMpr(3);
		} else if (cookingId == COOKING_1_3_N || cookingId == COOKING_1_3_S) { //개미다리 치즈구이
			cookingType = 3;
			pc.addAc(-1);
			pc.sendPackets(new S_OwnCharStatus(pc));
		} else if (cookingId == COOKING_1_4_N || cookingId == COOKING_1_4_S) { //과일 샐러드
			cookingType = 4;
			pc.addMaxMp(20);
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (cookingId == COOKING_1_5_N || cookingId == COOKING_1_5_S) { //과일 탕수육
			cookingType = 5;
			pc.addHpr(3);
		} else if (cookingId == COOKING_1_6_N || cookingId == COOKING_1_6_S) { //멧돼지 꼬치 구이
			cookingType = 6;
			pc.addMr(5);
			pc.sendPackets(new S_SPMR(pc));
		} else if (cookingId == COOKING_1_7_N || cookingId == COOKING_1_7_S) { //버섯 스프
			cookingType = 32;
		} else if (cookingId == COOKING_1_8_N || cookingId == COOKING_1_8_S) { //캐비어 카나페
			cookingType = 24;
			pc.addDmgup(1);
			pc.addHitup(1);
		} else if (cookingId == COOKING_1_9_N || cookingId == COOKING_1_9_S) { //악어 스테이크
			cookingType = 25;
			pc.addMaxHp(30);
			pc.addMaxMp(30);			
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			if (pc.isInParty()) { // 파티중
				pc.getParty().updateMiniHP(pc);
			}
		} else if (cookingId == COOKING_1_10_N || cookingId == COOKING_1_10_S) { //터틀 드래곤 과자
			cookingType = 26;
			pc.addAc(-2);
			pc.sendPackets(new S_OwnCharStatus(pc));			
		} else if (cookingId == COOKING_1_11_N || cookingId == COOKING_1_11_S) { //키위 패롯 구이
			cookingType = 27;
			pc.addBowHitRate(1);
			pc.addBowDmgup(1);
		} else if (cookingId == COOKING_1_12_N || cookingId == COOKING_1_12_S) { //스콜피온 구이
			cookingType = 28;
			pc.addHpr(2);
			pc.addMpr(2);
		} else if (cookingId == COOKING_1_13_N || cookingId == COOKING_1_13_S) { //일렉카둠 스튜
			cookingType = 29;
			pc.addMr(10);
			pc.sendPackets(new S_SPMR(pc));
		} else if (cookingId == COOKING_1_14_N || cookingId == COOKING_1_14_S) { //거미다리 꼬치구이
			cookingType = 30;
			pc.addSp(1);
			pc.sendPackets(new S_SPMR(pc));
			pc.sendPackets(new S_OwnCharStatus(pc));
		} else if (cookingId == COOKING_1_15_N || cookingId == COOKING_1_15_S) { //크랩살 스프
			cookingType = 32;
		} else if (cookingId == COOKING_1_16_N || cookingId == COOKING_1_16_S) { //크러스트시안 집게발 구이
			cookingType = 37;
			pc.addBowHitRate(2);
			pc.addBowDmgup(1);			
		} else if (cookingId == COOKING_1_17_N || cookingId == COOKING_1_17_S) { //그리폰 구이
			cookingType = 38;
			pc.addMaxHp(50);
			pc.addMaxMp(50);			
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			if (pc.isInParty()) { // 파티중
				pc.getParty().updateMiniHP(pc);
			}			
		} else if (cookingId == COOKING_1_18_N || cookingId == COOKING_1_18_S) { //코카트리스 스테이크
			cookingType = 39;
			pc.addDmgup(1);
			pc.addHitup(2);			
		} else if (cookingId == COOKING_1_19_N || cookingId == COOKING_1_19_S) { //대왕거북 구이
			cookingType = 40;
			pc.addAc(-3);
			pc.sendPackets(new S_OwnCharStatus(pc));
		} else if (cookingId == COOKING_1_20_N || cookingId == COOKING_1_20_S) { //레서 드래곤 날개 꼬치구이
			cookingType = 41;
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.addMr(15);
			pc.sendPackets(new S_SPMR(pc));			
			pc.sendPackets(new S_OwnCharAttrDef(pc));			
		} else if (cookingId == COOKING_1_21_N || cookingId == COOKING_1_21_S) { //드레이크 구이
			cookingType = 42;
			pc.addSp(2);
			pc.addMpr(2);
			pc.sendPackets(new S_SPMR(pc));
			pc.sendPackets(new S_OwnCharStatus(pc));			
		} else if (cookingId == COOKING_1_22_N || cookingId == COOKING_1_22_S) { //심해어 스튜
			cookingType = 43;
			pc.addHpr(2);
			pc.addMaxHp(30);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));			
		} else if (cookingId == COOKING_1_23_N || cookingId == COOKING_1_23_S) { //바실리스크 알 스프
			cookingType = 32;
		}
		pc.sendPackets(new S_PacketBox(53, cookingType, time));
		pc.setSkillEffect(cookingId, time * 1000);
		if (cookingId >= COOKING_1_0_N && cookingId <= COOKING_1_6_N
				|| cookingId >= COOKING_1_0_S && cookingId <= COOKING_1_6_S
				|| cookingId >= COOKING_1_8_N && cookingId <= COOKING_1_14_N   
				|| cookingId >= COOKING_1_8_S && cookingId <= COOKING_1_14_S
				|| cookingId >= COOKING_1_16_N && cookingId <= COOKING_1_22_N
				|| cookingId >= COOKING_1_16_S && cookingId <= COOKING_1_22_S) {
			pc.setCookingId(cookingId);
		} else if (cookingId == COOKING_1_7_N || cookingId == COOKING_1_7_S // 버섯 스프
				|| cookingId == COOKING_1_15_N || cookingId == COOKING_1_15_S // 크랩살 스프
				|| cookingId == COOKING_1_23_N || cookingId == COOKING_1_23_S) { // 바실리스크 알 스프
			pc.setDessertId(cookingId);
		}

		// 공복 게이지가17%가 되기 (위해)때문에 재발송신.S_PacketBox에 공복 게이지 갱신의 코드가 포함되어 있어?
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

}
