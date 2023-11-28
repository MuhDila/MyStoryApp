package com.muhdila.mystoryapp

import com.muhdila.mystoryapp.data.remote.response.ListStory

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStory>{
        val items : MutableList<ListStory> = arrayListOf()
        for (i in 0..100){
            val listStoryItem = ListStory(
                "https://story-api.dicoding.dev/images/stories/photos-1683497415212_dnKjbXF-.jpg",
                "2023-05-07T22:10:15.213Z",
                "Muh Dila",
                "Halo, semuanya",
                37.422092,
                "story-$i",
                -122.08392
            )
            items.add(listStoryItem)
        }
        return items
    }

}