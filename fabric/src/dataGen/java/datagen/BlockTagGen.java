package datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.BlockTags;
import tictim.paraglider.contents.Contents;
import tictim.paraglider.contents.ParagliderTags;

public class BlockTagGen extends FabricTagProvider.BlockTagProvider{
	public BlockTagGen(FabricDataGenerator output){
		super(output);
	}

	@Override
	protected void generateTags(){
		Contents contents = Contents.get();
		tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.add((contents.goddessStatue()),
						(contents.kakarikoGoddessStatue()),
						(contents.goronGoddessStatue()),
						(contents.ritoGoddessStatue()),
						(contents.hornedStatue()));

		tag(ParagliderTags.Blocks.STATUES_GODDESS).add(
				(contents.goddessStatue()),
				(contents.kakarikoGoddessStatue()),
				(contents.goronGoddessStatue()),
				(contents.ritoGoddessStatue()));
		tag(ParagliderTags.Blocks.STATUES)
				.add((contents.hornedStatue()))
				.addTag(ParagliderTags.Blocks.STATUES_GODDESS);
	}
}
