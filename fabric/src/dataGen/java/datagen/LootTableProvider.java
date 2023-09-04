package datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import tictim.paraglider.contents.Contents;

public class LootTableProvider extends FabricBlockLootTableProvider{
	public LootTableProvider(FabricDataGenerator dataOutput){
		super(dataOutput);
	}

	@Override public void generateBlockLootTables(){
		Contents contents = Contents.get();
		dropSelf(contents.goddessStatue());
		dropSelf(contents.goronGoddessStatue());
		dropSelf(contents.kakarikoGoddessStatue());
		dropSelf(contents.ritoGoddessStatue());
		dropSelf(contents.hornedStatue());
	}
}
