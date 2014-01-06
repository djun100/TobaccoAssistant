package com.asiaonline.tobaccoassistant.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.Context;
import android.database.Cursor;

import com.asiaonline.tobaccoassistant.Constant;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.entity.VillageListItem;

public class ContractDAO extends SQLiteHelper{
	public ContractDAO(Context context) {
		super(context);
	}
	/**插入数据
	 * */
	public boolean doInsert(Contract contract){
		String sql = null;
		boolean is = true;
		if(!isExist(contract)){
		sql = "insert into "+Constant.TABLENAME+" (name,pinyin,gender,image,indentityCard,birthday,national,address,villageName,familyMembers,workers,education,prevPlantType,plantTYArea,plantDYArea,bankCard,bankCardNo,signTime,longtitude,latitude,status)values('"+
		contract.getName()+"','"+
		getPinYin(contract.getName())+"','"+
		contract.getGender()+"','"+
		contract.getImage()+"','"+
		contract.getIndentityCard()+"','"+
		contract.getBirthday()+"','"+
		contract.getNational()+"','"+
		contract.getAddress()+"','"+
		contract.getVillageName()+"',"+
		contract.getFamilyMembers()+","+
		contract.getWorkers()+",'"+
		contract.getEducation()+"','"+
		contract.getPrevPlantType()+"',"+
		contract.getPlantTYArea()+","+
		contract.getPlantDYArea()+",'"+
		contract.getBankCard()+"','"+
		contract.getBankCardNo()+"','"+
		contract.getSignTime()+"','"+
		contract.getLongtitude()+"','"+
		contract.getLatitude()+"',"+
		contract.getStatus()+
		")";
		try {
				execInsertSql(sql);
			} catch (Exception e) {
				e.printStackTrace();
				is = false;
			}
		}else{
			doUpdate(contract);
		}
		return is;
		
	}
	
	/**根据身份证删除一个烟农
	 * */
	public boolean delete(String indentityCard){
		boolean is = true;
		String sql = "delete from "+Constant.TABLENAME+" where indentityCard='"+indentityCard+"'";
		try {
			execDeleteSql(sql);
		} catch (Exception e) {
			is = false;
			e.printStackTrace();
		}
		return is;
	}
	/**设置状态
	 * */
	public void setStatus(Contract contract){
		String sql = "update "+Constant.TABLENAME+" set status="+contract.getStatus()+" where indentityCard='"+contract.getIndentityCard()+"'";
		execSql(sql);
	}
	/**模糊查询
	 * */
	public List<Contract> findByName(String n,String v){
		List<Contract> list = new ArrayList<Contract>();
		String sql = "select * from "+Constant.TABLENAME+" where name like '%"+n+"%' and villageName='"+v+"' order by pinyin";
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String gender = cursor.getString(cursor.getColumnIndex("gender"));
			String image = cursor.getString(cursor.getColumnIndex("image"));
			String indentityCard = cursor.getString(cursor.getColumnIndex("indentityCard"));
			String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
			String national = cursor.getString(cursor.getColumnIndex("national"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String villageName = cursor.getString(cursor.getColumnIndex("villageName"));
			int familyMembers = cursor.getInt(cursor.getColumnIndex("familyMembers"));
			int workers = cursor.getInt(cursor.getColumnIndex("workers"));
			String education = cursor.getString(cursor.getColumnIndex("education"));
			String prevPlantType = cursor.getString(cursor.getColumnIndex("prevPlantType"));
			double plantTYArea = cursor.getDouble(cursor.getColumnIndex("plantTYArea"));
			double plantDYArea = cursor.getDouble(cursor.getColumnIndex("plantDYArea"));
			String bankCard = cursor.getString(cursor.getColumnIndex("bankCard"));
			String bankCardNo = cursor.getString(cursor.getColumnIndex("bankCardNo"));
			String signTime = cursor.getString(cursor.getColumnIndex("signTime"));
			String longtitude = cursor.getString(cursor.getColumnIndex("longtitude"));
			String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			list.add(new Contract(id, name, gender, image, indentityCard, birthday, national, address, villageName, familyMembers, workers, education, prevPlantType, plantTYArea, plantDYArea, bankCard, bankCardNo, signTime, longtitude, latitude, status));
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return list;
	}
	/**模糊查询n条 
	 * */
	public List<Contract> findByName(String n,int num){
		List<Contract> list = new ArrayList<Contract>();
		String sql = "select * from "+Constant.TABLENAME+" where name like '%"+n+"%' order by pinyin limit "+num;
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String gender = cursor.getString(cursor.getColumnIndex("gender"));
			String image = cursor.getString(cursor.getColumnIndex("image"));
			String indentityCard = cursor.getString(cursor.getColumnIndex("indentityCard"));
			String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
			String national = cursor.getString(cursor.getColumnIndex("national"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String villageName = cursor.getString(cursor.getColumnIndex("villageName"));
			int familyMembers = cursor.getInt(cursor.getColumnIndex("familyMembers"));
			int workers = cursor.getInt(cursor.getColumnIndex("workers"));
			String education = cursor.getString(cursor.getColumnIndex("education"));
			String prevPlantType = cursor.getString(cursor.getColumnIndex("prevPlantType"));
			double plantTYArea = cursor.getDouble(cursor.getColumnIndex("plantTYArea"));
			double plantDYArea = cursor.getDouble(cursor.getColumnIndex("plantDYArea"));
			String bankCard = cursor.getString(cursor.getColumnIndex("bankCard"));
			String bankCardNo = cursor.getString(cursor.getColumnIndex("bankCardNo"));
			String signTime = cursor.getString(cursor.getColumnIndex("signTime"));
			String longtitude = cursor.getString(cursor.getColumnIndex("longtitude"));
			String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			list.add(new Contract(id, name, gender, image, indentityCard, birthday, national, address, villageName, familyMembers, workers, education, prevPlantType, plantTYArea, plantDYArea, bankCard, bankCardNo, signTime, longtitude, latitude, status));
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return list;
	}
	
	/**根据身份证号查询
	 * */
	public Contract findById(String n){
		Contract contract = null;
		String sql = "select * from "+Constant.TABLENAME+" where indentityCard = '"+n+"'";
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String gender = cursor.getString(cursor.getColumnIndex("gender"));
			String image = cursor.getString(cursor.getColumnIndex("image"));
			String indentityCard = cursor.getString(cursor.getColumnIndex("indentityCard"));
			String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
			String national = cursor.getString(cursor.getColumnIndex("national"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String villageName = cursor.getString(cursor.getColumnIndex("villageName"));
			int familyMembers = cursor.getInt(cursor.getColumnIndex("familyMembers"));
			int workers = cursor.getInt(cursor.getColumnIndex("workers"));
			String education = cursor.getString(cursor.getColumnIndex("education"));
			String prevPlantType = cursor.getString(cursor.getColumnIndex("prevPlantType"));
			double plantTYArea = cursor.getDouble(cursor.getColumnIndex("plantTYArea"));
			double plantDYArea = cursor.getDouble(cursor.getColumnIndex("plantDYArea"));
			String bankCard = cursor.getString(cursor.getColumnIndex("bankCard"));
			String bankCardNo = cursor.getString(cursor.getColumnIndex("bankCardNo"));
			String signTime = cursor.getString(cursor.getColumnIndex("signTime"));
			String longtitude = cursor.getString(cursor.getColumnIndex("longtitude"));
			String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			contract = new Contract(id, name, gender, image, indentityCard, birthday, national, address, villageName, familyMembers, workers, education, prevPlantType, plantTYArea, plantDYArea, bankCard, bankCardNo, signTime, longtitude, latitude, status);
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return contract;
	}
	/**降序查询几条
	 * */
	public List<Contract> findTop(int start,int num){
		if(start<0){
			start = 0;
		}
		List<Contract> list = new ArrayList<Contract>();
		String sql = "select * from "+Constant.TABLENAME+" order by id desc limit "+num+" offset "+start;
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String gender = cursor.getString(cursor.getColumnIndex("gender"));
			String image = cursor.getString(cursor.getColumnIndex("image"));
			String indentityCard = cursor.getString(cursor.getColumnIndex("indentityCard"));
			String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
			String national = cursor.getString(cursor.getColumnIndex("national"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String villageName = cursor.getString(cursor.getColumnIndex("villageName"));
			int familyMembers = cursor.getInt(cursor.getColumnIndex("familyMembers"));
			int workers = cursor.getInt(cursor.getColumnIndex("workers"));
			String education = cursor.getString(cursor.getColumnIndex("education"));
			String prevPlantType = cursor.getString(cursor.getColumnIndex("prevPlantType"));
			double plantTYArea = cursor.getDouble(cursor.getColumnIndex("plantTYArea"));
			double plantDYArea = cursor.getDouble(cursor.getColumnIndex("plantDYArea"));
			String bankCard = cursor.getString(cursor.getColumnIndex("bankCard"));
			String bankCardNo = cursor.getString(cursor.getColumnIndex("bankCardNo"));
			String signTime = cursor.getString(cursor.getColumnIndex("signTime"));
			String longtitude = cursor.getString(cursor.getColumnIndex("longtitude"));
			String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			list.add(new Contract(id, name, gender, image, indentityCard, birthday, national, address, villageName, familyMembers, workers, education, prevPlantType, plantTYArea, plantDYArea, bankCard, bankCardNo, signTime, longtitude, latitude, status));
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return list;
	}
	/**更新数据
	 * */
	public void doUpdate(Contract contract){
		String sql = "update "+Constant.TABLENAME +" set "+
				"name='"+contract.getName()+"',"+
				"pinyin='"+getPinYin(contract.getName())+"',"+
				"gender='"+contract.getGender()+"',"+
				"image='"+contract.getImage()+"',"+
				"indentityCard='"+contract.getIndentityCard()+"',"+
				"birthday='"+contract.getBirthday()+"',"+
				"national='"+contract.getNational()+"',"+
				"address='"+contract.getAddress()+"',"+
				"villageName='"+contract.getVillageName()+"',"+
				"familyMembers="+contract.getFamilyMembers()+","+
				"workers="+contract.getWorkers()+","+
				"education='"+contract.getEducation()+"',"+
				"prevPlantType='"+contract.getPrevPlantType()+"',"+
				"plantTYArea="+contract.getPlantTYArea()+","+
				"plantDYArea="+contract.getPlantDYArea()+","+
				"bankCard='"+contract.getBankCard()+"',"+
				"bankCardNo='"+contract.getBankCardNo()+"',"+
				"signTime='"+contract.getSignTime()+"',"+
				"longtitude='"+contract.getLongtitude()+"',"+
				"latitude='"+contract.getLatitude()+"',"+
				"status="+contract.getStatus()+
				" where id="+contract.getId();
		execSql(sql);
	}
	/**根据村委查烟农
	 * */
	public List<Contract> getFarmersByVillageName(String pname){
		List<Contract> list = new ArrayList<Contract>();
		String sql = "select * from "+Constant.TABLENAME+" where villageName='"+pname+"' order by pinyin";
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String gender = cursor.getString(cursor.getColumnIndex("gender"));
			String image = cursor.getString(cursor.getColumnIndex("image"));
			String indentityCard = cursor.getString(cursor.getColumnIndex("indentityCard"));
			String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
			String national = cursor.getString(cursor.getColumnIndex("national"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String villageName = cursor.getString(cursor.getColumnIndex("villageName"));
			int familyMembers = cursor.getInt(cursor.getColumnIndex("familyMembers"));
			int workers = cursor.getInt(cursor.getColumnIndex("workers"));
			String education = cursor.getString(cursor.getColumnIndex("education"));
			String prevPlantType = cursor.getString(cursor.getColumnIndex("prevPlantType"));
			double plantTYArea = cursor.getDouble(cursor.getColumnIndex("plantTYArea"));
			double plantDYArea = cursor.getDouble(cursor.getColumnIndex("plantDYArea"));
			String bankCard = cursor.getString(cursor.getColumnIndex("bankCard"));
			String bankCardNo = cursor.getString(cursor.getColumnIndex("bankCardNo"));
			String signTime = cursor.getString(cursor.getColumnIndex("signTime"));
			String longtitude = cursor.getString(cursor.getColumnIndex("longtitude"));
			String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			list.add(new Contract(id, name, gender, image, indentityCard, birthday, national, address, villageName, familyMembers, workers, education, prevPlantType, plantTYArea, plantDYArea, bankCard, bankCardNo, signTime, longtitude, latitude, status));
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return list;
	}
	/**根据村委和状态查烟农
	 * */
	public List<Contract> getFarmers(String n,int s){
		List<Contract> list = new ArrayList<Contract>();
		String sql = "select * from "+Constant.TABLENAME+" where villageName='"+n+"' and status = "+s+" order by pinyin";
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String gender = cursor.getString(cursor.getColumnIndex("gender"));
			String image = cursor.getString(cursor.getColumnIndex("image"));
			String indentityCard = cursor.getString(cursor.getColumnIndex("indentityCard"));
			String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
			String national = cursor.getString(cursor.getColumnIndex("national"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String villageName = cursor.getString(cursor.getColumnIndex("villageName"));
			int familyMembers = cursor.getInt(cursor.getColumnIndex("familyMembers"));
			int workers = cursor.getInt(cursor.getColumnIndex("workers"));
			String education = cursor.getString(cursor.getColumnIndex("education"));
			String prevPlantType = cursor.getString(cursor.getColumnIndex("prevPlantType"));
			double plantTYArea = cursor.getDouble(cursor.getColumnIndex("plantTYArea"));
			double plantDYArea = cursor.getDouble(cursor.getColumnIndex("plantDYArea"));
			String bankCard = cursor.getString(cursor.getColumnIndex("bankCard"));
			String bankCardNo = cursor.getString(cursor.getColumnIndex("bankCardNo"));
			String signTime = cursor.getString(cursor.getColumnIndex("signTime"));
			String longtitude = cursor.getString(cursor.getColumnIndex("longtitude"));
			String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			list.add(new Contract(id, name, gender, image, indentityCard, birthday, national, address, villageName, familyMembers, workers, education, prevPlantType, plantTYArea, plantDYArea, bankCard, bankCardNo, signTime, longtitude, latitude, status));
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return list;
	}
	/**根据村委和名字查烟农
	 * */
	public List<Contract> getFarmers(String n,String v){
		List<Contract> list = new ArrayList<Contract>();
		String sql = "select * from "+Constant.TABLENAME+" where villageName='"+v+"' and name= '"+n+"' order by pinyin";
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String gender = cursor.getString(cursor.getColumnIndex("gender"));
			String image = cursor.getString(cursor.getColumnIndex("image"));
			String indentityCard = cursor.getString(cursor.getColumnIndex("indentityCard"));
			String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
			String national = cursor.getString(cursor.getColumnIndex("national"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String villageName = cursor.getString(cursor.getColumnIndex("villageName"));
			int familyMembers = cursor.getInt(cursor.getColumnIndex("familyMembers"));
			int workers = cursor.getInt(cursor.getColumnIndex("workers"));
			String education = cursor.getString(cursor.getColumnIndex("education"));
			String prevPlantType = cursor.getString(cursor.getColumnIndex("prevPlantType"));
			double plantTYArea = cursor.getDouble(cursor.getColumnIndex("plantTYArea"));
			double plantDYArea = cursor.getDouble(cursor.getColumnIndex("plantDYArea"));
			String bankCard = cursor.getString(cursor.getColumnIndex("bankCard"));
			String bankCardNo = cursor.getString(cursor.getColumnIndex("bankCardNo"));
			String signTime = cursor.getString(cursor.getColumnIndex("signTime"));
			String longtitude = cursor.getString(cursor.getColumnIndex("longtitude"));
			String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			list.add(new Contract(id, name, gender, image, indentityCard, birthday, national, address, villageName, familyMembers, workers, education, prevPlantType, plantTYArea, plantDYArea, bankCard, bankCardNo, signTime, longtitude, latitude, status));
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return list;
	}
	
	/**获取村委
	 * */
	public List<VillageListItem> getVillage(){
		List<VillageListItem> list = new ArrayList<VillageListItem>();
		String sql = "select * from "+Constant.TABLENAME;
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			String villageName = cursor.getString(cursor.getColumnIndex("villageName"));
			double plantTYArea = cursor.getFloat(cursor.getColumnIndex("plantTYArea"));
			double plantDYArea = cursor.getFloat(cursor.getColumnIndex("plantDYArea"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			boolean isExist = false;
			for(int i=0;i<list.size();i++){
				if(list.get(i).getVillageName().equalsIgnoreCase(villageName)){
					isExist = true;
					if(status==Contract.YES){
						list.get(i).setCount(list.get(i).getCount()+1);
						list.get(i).setTotalPlantArea(sum(list.get(i).getTotalPlantArea(),plantTYArea,plantDYArea));
					}
				}
			}
			if(!isExist){
				if(status==Contract.YES){
					list.add(new VillageListItem(villageName, 1, sum(0,plantTYArea,plantDYArea)));
				}else{
					list.add(new VillageListItem(villageName, 0,0));
				}
				
			}
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return list;
	}
	/**获取烟农总数
	 * */
	public int getTotalCount(){
		String sql = "select * from "+Constant.TABLENAME;
		Cursor cursor = Query(sql);
		return cursor.getCount();
	}
	public boolean isExist(Contract contract){
		boolean exist = false;
		String sql = "select * from "+Constant.TABLENAME+ " where indentityCard='"+contract.getIndentityCard()+"'";
		Cursor cursor = Query(sql);
		if(cursor!=null&&cursor.moveToNext()){
			exist = true;
			cursor.close();
		}
		super.closeConnection();
		return exist;
	}
	/**  
     * double 相加  
     * @param d1  
     * @param d2  
     * @return  
     */  
    public double sum(double total,double ty,double dy){  
        BigDecimal totalarea = new BigDecimal(Double.toString(total));  
        BigDecimal tyarea = new BigDecimal(Double.toString(ty));  
        BigDecimal dyarea = new BigDecimal(Double.toString(dy));  
        return totalarea.add(tyarea).add(dyarea).doubleValue();  
    }  
	private String getPinYin(String str){
		StringBuffer pybf = new StringBuffer(); 
        char[] arr = str.toCharArray(); 
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat(); 
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE); 
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE); 
        for (int i = 0; i < arr.length; i++) { 
                if (arr[i] > 128) { 
                        try { 
                                pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]); 
                        } catch (BadHanyuPinyinOutputFormatCombination e) { 
                                e.printStackTrace(); 
                        } 
                } else { 
                        pybf.append(arr[i]); 
                } 
        } 
        return pybf.toString(); 
	}
}
