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

	public void SerializeObject(Object obj, String objName) {
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

	public EnemyData DeserializeEnemyData(String enemyName) {
		EnemyData enemyData = null;
		try {
			enemyData = new EnemyData();
			String objFilepath = "resources/enemyXMLs/" + enemyName;
			fis = new FileInputStream(objFilepath);
			xmld = new XMLDecoder(fis);
			enemyData = (EnemyData) xmld.readObject();
			xmld.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return enemyData;
	}

	public ParticleData DeserializeParticleData(String particleType) {
		ParticleData particleData = null;
		try {
			particleData = new ParticleData();
			String objFilepath = "resources/particleXMLs/" + particleType;
			fis = new FileInputStream(objFilepath);
			xmld = new XMLDecoder(fis);
			particleData = (ParticleData) xmld.readObject();
			xmld.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return particleData;
	}

	public static XMLSerializer getInstance() {
		return instance;
	}

}
