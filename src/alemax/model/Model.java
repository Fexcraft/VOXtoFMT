package alemax.model;

import java.util.ArrayList;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import alemax.vox.*;

public class Model {
	
	private ArrayList<StrippedChunk> initialChunks;
	public ArrayList<Chunk> chunks;
	public Vector4f[] colors;
	private VoxChunkMain main;
	
	public Model(byte[] voxData) {
		main = new VoxChunkMain(voxData, 8);
		initialChunks = new ArrayList<StrippedChunk>();
		for(VoxChunk voxChunk : main.childrenChunks) {
			if(voxChunk instanceof VoxChunkSize) {
				initialChunks.add(new StrippedChunk());
				initialChunks.get(initialChunks.size() - 1).setSize(((VoxChunkSize) voxChunk).sizeX, ((VoxChunkSize) voxChunk).sizeY, ((VoxChunkSize) voxChunk).sizeZ);
			} else if(voxChunk instanceof VoxChunkXYZI) {
				initialChunks.get(initialChunks.size() - 1).setVoxels(((VoxChunkXYZI) voxChunk).voxels);
			} else if(voxChunk instanceof VoxChunkRGBA) {
				this.colors = ((VoxChunkRGBA) voxChunk).palette;
			}
		}
		
		chunks = new ArrayList<Chunk>();
		ArrayList<Integer> childIDs = new ArrayList<Integer>();
		
		for(VoxChunk voxChunk : main.childrenChunks) {
			if(voxChunk instanceof VoxChunkNGRP) {
				for(int i = 0; i < ((VoxChunkNGRP) voxChunk).childNodeIDs.length; i++) {
					childIDs.add(((VoxChunkNGRP) voxChunk).childNodeIDs[i]);
				}
			}
		}
		
		for(VoxChunk voxChunk : main.childrenChunks) {
			if(voxChunk instanceof VoxChunkNTRN) {
				int nodeID = ((VoxChunkNTRN) voxChunk).nodeID;
				boolean found = false;
				for(Integer id : childIDs) {
					if(id.intValue() == nodeID) {
						found = true;
						break;
					}
				}
				if(!found) {
					Vector3f translation = new Vector3f();
					Matrix3f rotation = new Matrix3f();
					goDownNodes(nodeID, translation, rotation);
					break;
				}
			}
		}
	}
	
	private void goDownNodes(int nodeID, Vector3f translation, Matrix3f rotation) {
		
		Vector3f newTranslation = new Vector3f(translation);
		Matrix3f newRotation = new Matrix3f(rotation);
		
		for(VoxChunk voxChunk : main.childrenChunks) {
			if(voxChunk instanceof VoxChunkNTRN) {
				if(((VoxChunkNTRN) voxChunk).nodeID == nodeID) {		
					
					if(((VoxChunkNTRN) voxChunk).frameAttributes[0].map.containsKey("_r")) {
						String rotationString = ((VoxChunkNTRN) voxChunk).frameAttributes[0].map.get("_r").string;
						
						byte rotationByte = (byte) Integer.parseInt(rotationString);
						VoxRotation rot = new VoxRotation(rotationByte);
						newRotation.mul(rot.rotMatrix);
					}
					
					if(((VoxChunkNTRN) voxChunk).frameAttributes[0].map.containsKey("_t")) {
						String translationString = ((VoxChunkNTRN) voxChunk).frameAttributes[0].map.get("_t").string;
						
						//HERE?? yes...
						
						
						
						String[] translationSplit = translationString.split(" ");
						newTranslation.add(new Vector3f(Integer.parseInt(translationSplit[0]), Integer.parseInt(translationSplit[1]), Integer.parseInt(translationSplit[2]))); 
					}
					
					goDownNodes(((VoxChunkNTRN) voxChunk).childNodeID, newTranslation, newRotation);
					
				}
			}
			if(voxChunk instanceof VoxChunkNGRP) {
				if(((VoxChunkNGRP) voxChunk).nodeID == nodeID) {
					for(int i = 0; i < ((VoxChunkNGRP) voxChunk).childNodeIDs.length; i++) {
						goDownNodes(((VoxChunkNGRP) voxChunk).childNodeIDs[i], newTranslation, newRotation);
					}
				}
			}
			if(voxChunk instanceof VoxChunkNSHP) {
				if(((VoxChunkNSHP) voxChunk).nodeID == nodeID) {
					//rotation.transpose();
					int modelID = ((VoxChunkNSHP) voxChunk).modelIDs[0];
					
					chunks.add(new Chunk());
					chunks.get(chunks.size() - 1).setSize(initialChunks.get(modelID).getSizeX(), initialChunks.get(modelID).getSizeY(), initialChunks.get(modelID).getSizeZ());
					chunks.get(chunks.size() - 1).setVoxels(initialChunks.get(modelID).getVoxels());
					
					chunks.get(chunks.size() - 1).setTranslation((int) Math.round(newTranslation.x), (int) Math.round(newTranslation.y), (int) Math.round(newTranslation.z));
					chunks.get(chunks.size() - 1).setRotation(newRotation);
					chunks.get(chunks.size() - 1).bake();
				}
			}
		}
	}
	
}
