package alemax.vox;

import alemax.util.ByteHandler;

public class VoxString extends VoxPart {

	public String string;
	
	public VoxString(byte[] voxData, int index) {
		super(voxData, index);
		int bufferSize = ByteHandler.getInt32(ByteHandler.getSubArray(voxData, index, 4));
		string = bufferSize > 0 ? ByteHandler.getString(ByteHandler.getSubArray(voxData, index + 4, bufferSize)) : "";
		rawData = ByteHandler.getSubArray(voxData, index, bufferSize + 4);
	}
	
	public static VoxString create(byte[] voxData, int index) {
		VoxString voxString = new VoxString(voxData, index);
		int bufferSize = ByteHandler.getInt32(ByteHandler.getSubArray(voxData, index, 4));
		voxString.string = bufferSize > 0 ? ByteHandler.getString(ByteHandler.getSubArray(voxData, index + 4, bufferSize)) : "";
		voxString.rawData = ByteHandler.getSubArray(voxData, index, bufferSize + 4);
		return voxString;
	}
	
}
