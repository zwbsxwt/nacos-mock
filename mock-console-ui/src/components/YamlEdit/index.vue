<template>
  <div class="json-editor">
    <textarea ref="textarea" />
  </div>
</template>

<script>
import CodeMirror from 'codemirror'
import 'codemirror/lib/codemirror.css'
// 替换主题这里需修改名称
import 'codemirror/theme/idea.css'
import 'codemirror/mode/yaml/yaml'
export default {
  props: {
    content: {
      type: String,
      required: false
    },
    height: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      editor: false
    }
  },
  watch: {
    content(value) {
      const editorValue = this.editor.getValue()
      if (value !== editorValue) {
        this.editor.setValue(this.content)
      }
    },
    height(value) {
      this.editor.setSize('auto', this.height)
    }
  },
  mounted() {
    this.editor = CodeMirror.fromTextArea(this.$refs.textarea, {
      mode: 'text/x-yaml',
      lineNumbers: true,
      lint: true,
      lineWrapping: true,
      tabSize: 2,
      cursorHeight: 0.9,
      // 替换主题这里需修改名称
      theme: 'idea'
    })
    this.editor.setSize('auto', this.height)
    this.editor.setValue(this.content)
    this.editor.on('change', cm => {
      this.$emit('changed', cm.getValue())
      this.$emit('input', cm.getValue())
    })
  },
  methods: {
    // setContent() {
    //   this.editor.setValue(this.content)
    // },
    getContent() {
      return this.editor.getValue()
    }
  }
}
</script>

<style scoped>
  .json-editor{
    height: 100%;
    margin-bottom: 10px;
  }
  .json-editor >>> .CodeMirror {
    font-size: 13px;
    overflow-y:auto;
    font-weight:normal
  }
  .json-editor >>> .CodeMirror-scroll{
  }
  .json-editor >>> .cm-s-rubyblue span.cm-string {
    color: #F08047;
  }
</style>
