package com.example.alucut.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TemplateRepository(context: Context) {
    private val prefs = context.getSharedPreferences("alucut_templates", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getTemplates(): List<Template> {
        val json = prefs.getString("templates", null)
        return if (json.isNullOrEmpty()) {
            val defaults = TemplateDefaults.defaultTemplates()
            saveTemplates(defaults)
            defaults
        } else {
            val type = object : TypeToken<List<Template>>() {}.type
            gson.fromJson<List<Template>>(json, type) ?: TemplateDefaults.defaultTemplates()
        }
    }

    fun saveTemplates(templates: List<Template>) {
        prefs.edit().putString("templates", gson.toJson(templates)).apply()
    }

    fun addTemplate(t: Template) {
        val list = getTemplates().toMutableList()
        list.add(t)
        saveTemplates(list)
    }

    fun updateTemplate(t: Template) {
        val list = getTemplates().toMutableList()
        val idx = list.indexOfFirst { it.id == t.id }
        if (idx >= 0) {
            list[idx] = t
            saveTemplates(list)
        }
    }

    fun deleteTemplate(id: String) {
        val list = getTemplates().filterNot { it.id == id }
        saveTemplates(list)
    }
}
