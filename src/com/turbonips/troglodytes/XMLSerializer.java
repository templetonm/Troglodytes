package com.turbonips.troglodytes;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

public class XMLSerializer {
	private FileOutputStream fos;
	private XMLEncoder xmle;
	private FileInputStream fis;
	private XMLDecoder xmld;
	private static final XMLSerializer instance = new XMLSerializer();

	private XMLSerializer() {
	}

	public void serializeObject(Object obj, String objName) {
		try {
			String objFilepath = objName;
			fos = new FileOutputStream(objFilepath);
			xmle = new XMLEncoder(fos);
			xmle.writeObject(obj);
			xmle.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object deserializeData(String path) {
		Object data = null;
		try {
			data = new EnemyData();
			String objFilepath = path;
			fis = new FileInputStream(objFilepath);
			xmld = new XMLDecoder(fis);
			data = (Object) xmld.readObject();
			xmld.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public static XMLSerializer getInstance() {
		return instance;
	}

}
