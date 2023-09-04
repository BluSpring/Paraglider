package datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import tictim.paraglider.contents.Contents;
import tictim.paraglider.contents.ParagliderTags;

public class ItemTagGen extends FabricTagProvider.ItemTagProvider{
	public ItemTagGen(FabricDataGenerator output){
		super(output, new BlockTagGen(output)); // ?
	}

	@Override protected void generateTags(){
		copy(ParagliderTags.Blocks.STATUES, ParagliderTags.STATUES);
		copy(ParagliderTags.Blocks.STATUES_GODDESS, ParagliderTags.STATUES_GODDESS);

		Contents contents = Contents.get();
		tag(ParagliderTags.PARAGLIDERS).add((contents.paraglider()), (contents.dekuLeaf()));
	}
}
