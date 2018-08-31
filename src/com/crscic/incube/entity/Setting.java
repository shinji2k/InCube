package com.crscic.incube.entity;

/**
 * 
 * @author zhaokai
 * 2018年8月14日 下午5:43:20
 */
public class Setting
{
	private SettingEntity typeSetting;
	private SettingEntity classSetting;
	private SettingEntity checkSetting;
	public SettingEntity getTypeSetting()
	{
		return typeSetting;
	}
	public void setTypeSetting(SettingEntity typeSetting)
	{
		this.typeSetting = typeSetting;
	}
	public SettingEntity getClassSetting()
	{
		return classSetting;
	}
	public void setClassSetting(SettingEntity classSetting)
	{
		this.classSetting = classSetting;
	}
	public SettingEntity getCheckSetting()
	{
		return checkSetting;
	}
	public void setCheckSetting(SettingEntity checkSetting)
	{
		this.checkSetting = checkSetting;
	}
}
