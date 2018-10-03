package models

/*
    Here there is something that bothers me:
    Instagram's API endpoint requests returns with an Envelope containing Meta, Data and
    Pagination properties, like described below.
    As the value of Data may change, I could think about 2 solutions:
    1: it would be nice to have an Envelope data class with a wildcard Data property, OR
    2: use Retrofit's Converters to handle envelopes.

    The problem here is that the 1st solution doesn't work, as Retrofit's serialization does not
    allow wild cards, and the 2nd solution wouldn't be the best, as some responses may not contain
    the envelope (like the Auth flow, for example. Code and Token are directly sent to us, no
    envelope used). It would require another instance of Retrofit without the Converters.

    TL;DR: Retrofit and Envelopes are not cool in this case. Creating an EnvelopeMedia data class.
 */

data class EnvelopeMediaModel(
        val meta: MetaModel,
        val data: List<MediaModel>,
        val pagination: PaginationModel
)

data class MetaModel(
        val code: Integer
)

data class PaginationModel(
        val next_url: String,
        val next_max_id: String
)