package cn.edu.thu.hxd.homework.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.imageanalysis.ColorLayout;
import net.semanticmetadata.lire.imageanalysis.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.FCTH;
import net.semanticmetadata.lire.imageanalysis.sift.Extractor;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;


public class ImageUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static double[] easyGetFeatures(File imageFile) throws IOException{
		ByteArrayOutputStream outputStream=new ByteArrayOutputStream(100000);
		BufferedImage image = ImageIO.read(new FileInputStream(imageFile));
		ColorLayout p1 = new ColorLayout();
		p1.extract(image);
		outputStream.write(ByteBuffer.allocate(4).putInt(p1.getByteArrayRepresentation().length).array());

		FCTH f1 = new FCTH();
		f1.extract(image);
		outputStream.write(ByteBuffer.allocate(4).putInt(f1.getByteArrayRepresentation().length).array());

		EdgeHistogram eh1 = new EdgeHistogram();
		eh1.extract(image);
		outputStream.write(ByteBuffer.allocate(4).putInt(eh1.getByteArrayRepresentation().length).array());

		CEDD cedd = new CEDD();
		cedd.extract(image);
		outputStream.write(ByteBuffer.allocate(4).putInt(cedd.getByteArrayRepresentation().length).array());

		Extractor ex = new Extractor();
		List<Feature> features = ex.computeSiftFeatures(image);// 提取特征，
		outputStream.write(ByteBuffer.allocate(4).putInt(features.size()).array());
		outputStream.write(ByteBuffer.allocate(4).putInt(528).array());

		outputStream.write(p1.getByteArrayRepresentation());
		outputStream.write(f1.getByteArrayRepresentation());
		outputStream.write(eh1.getByteArrayRepresentation());
		outputStream.write(cedd.getByteArrayRepresentation());
		for(Feature f:features){
			outputStream.write(f.getByteArrayRepresentation());
		}
		return SequenceFileReaderUtils.transferFrameFeature2Bytes(outputStream.toByteArray());

	}
}
