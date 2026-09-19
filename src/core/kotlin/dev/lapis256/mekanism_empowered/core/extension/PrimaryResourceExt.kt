package dev.lapis256.mekanism_empowered.core.extension

import mekanism.common.resource.PrimaryResource
import mekanism.common.resource.ResourceType
import mekanism.common.tags.MekanismTags


fun PrimaryResource.processedTag(type: ResourceType) = MekanismTags.Items.PROCESSED_RESOURCES.get(type, this)!!
