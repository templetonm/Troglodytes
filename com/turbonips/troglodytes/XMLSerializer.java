package com.turbonips.troglodytes;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

import com.turbonips.troglodytes.entities.EnemyData;

public class XMLSerializer {
	private FileOutputStream fos;
	private XMLEncoder xmle;
	private FileInputStream fis;
	private XMLDecoder xmld;

	public XMLSerializer() {
		
	}
	
	public void SerializeObject(Object obj, String objName) {
		try
		{
			String objFilepath = objName; // TODO: Organize filepaths for XML files.
			fos = new FileOutputStream(objFilepath);
			xmle = new XMLEncoder(fos);
			xmle.writeObject(obj);
			xmle.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public EnemyData DeserializeEnemyData(EnemyData enemyData, String enemyName) {
		try
		{
			String objFilepath = enemyName; // See above todo message.
			fis = new FileInputStream(objFilepath);
			xmld = new XMLDecoder(fis);
			enemyData = (EnemyData) xmld.readObject();
			xmld.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return enemyData;
	}
	
}
