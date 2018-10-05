package models

/*
    Similar to EnvelopeMediaModel, but this time expecting a Tag.
 */

data class EnvelopeTagModel(
        val meta: MetaModel,
        val data: List<TagModel>,
        val pagination: PaginationModel
)