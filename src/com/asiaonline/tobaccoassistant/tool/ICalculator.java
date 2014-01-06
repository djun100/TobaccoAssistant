package com.asiaonline.tobaccoassistant.tool;

public interface ICalculator {
	/**
	 * 根据种植面积计算所需烟苗数量
	 * @param plantArea	种植面积
	 */
	public int calculatePlantNum(float plantArea);
	
	/**
	 * 根据生产量计算合同总量
	 * @param throughput 生产量
	 */
	public float calculateYield(float throughput);
	/**
	 * 根据种植面积和亩产计算生产量
	 * @param plantArea
	 * @param yieldPerMu
	 */
	public float calculateThroughput(float plantArea,float yieldPerMu);
	/**
	 * 根据合同总量计算指令性计划
	 * @param yield	合同总量
	 */
	public float calculateOrderYield(float yield);
	/**
	 * 根据合同总量计算出口备货计划
	 * @param yield	合同总量
	 */
	public float calculateExportYield(float yield,float orderYield);
	
}
