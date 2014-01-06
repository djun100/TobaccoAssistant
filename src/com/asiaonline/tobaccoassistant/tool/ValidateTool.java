package com.asiaonline.tobaccoassistant.tool;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.TextView;


public class ValidateTool {
	/**
	 * 未录入信息检验
	 * @return
	 */
	public static boolean isEmptyValidate(List<? extends TextView> list,String erroMsg){
		boolean isValidate = true;
		for(int i = 0 ; i < list.size() ; i++){
			TextView tv = list.get(i);
			if(tv.getText().toString().trim().equals("") ){
				tv.setError(erroMsg);
				isValidate = false;
			}
		}
		return isValidate;
	}
	/**
	 * 浮点数校验，大于等于0，且最多保留两位小数
	 * @return
	 */
	public static boolean isFloateValidate(List<? extends TextView> list,String erroMsg){
		boolean isValidate = true;
		Pattern pattern = Pattern.compile("^(?:0|[1-9]\\d*)(\\.\\d{0,2})?$");
		for(int i = 0 ; i < list.size() ; i++){
			TextView tv = list.get(i);
			String content = tv.getText().toString().trim();
			if(content.equals("")){
				continue;
			}
			Matcher matcher = pattern.matcher(content);
			if(!matcher.matches()){
				tv.setError(erroMsg);
				isValidate = false;
			}
		}
		return isValidate;
	}
	/**
	 * 正整数
	 * @return
	 */
	public static boolean isPositiveIntegerValidate(List<? extends TextView> list,String erroMsg){
		boolean isValidate = true;
		Pattern pattern = Pattern.compile("[1-9]{1}[0-9]*");
		for(int i = 0 ; i < list.size() ; i++){
			TextView tv = list.get(i);
			String content = tv.getText().toString().trim();
			if(content.equals("")){
				continue;
			}
			Matcher matcher = pattern.matcher(content);
			if(!matcher.matches()){
				tv.setError(erroMsg);
				isValidate = false;
			}
		}
		return isValidate;
	}
}
