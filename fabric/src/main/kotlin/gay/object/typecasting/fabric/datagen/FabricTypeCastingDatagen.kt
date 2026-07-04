package gay.`object`.typecasting.fabric.datagen

import gay.`object`.typecasting.datagen.TypeCastingActionTags
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object FabricTypeCastingDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack = gen.createPack()

        pack.addProvider(::TypeCastingActionTags)
    }
}
