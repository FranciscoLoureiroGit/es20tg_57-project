<template>
    <v-dialog
            :value="dialog"
            @input="$emit('dialog', false)"
            @keydown.esc="$emit('dialog', false)"
            max-width="75%"
            max-height="80%"
    >
        <v-card>
            <v-card-title>
        <span class="headline">
          {{
            editQuestion && editQuestion.id === null
              ? 'New Question'
              : 'Edit Question'
          }}
        </span>
            </v-card-title>

            <v-card-text class="text-left" v-if="editQuestion">
                <v-container grid-list-md fluid>
                    <v-layout column wrap>
                        <v-flex xs24 sm12 md8>
                            <v-text-field v-model="editQuestion.title" label="Title"
                                          data-cy="QuestionTitle"/>
                        </v-flex>
                        <v-flex xs24 sm12 md12>
                            <v-textarea
                                    rows="10"
                                    v-model="editQuestion.content"
                                    label="Content"
                                    data-cy="content"
                            ></v-textarea>
                        </v-flex>
                        <v-flex
                                xs24
                                sm12
                                md12
                        >
                            <v-switch
                                    v-model="editQuestion.options[0].correct"
                                    class="ma-4"
                                    label="Correct"
                                    data-cy="option1"
                            />
                            <v-textarea
                                    outline="textarea1"
                                    rows="10"
                                    v-model="editQuestion.options[0].content"
                                    label="Content 1"
                                    data-cy="content1"
                            ></v-textarea>
                        </v-flex>

                        <v-flex
                                xs24
                                sm12
                                md12
                        >
                            <v-switch
                                    v-model="editQuestion.options[1].correct"
                                    class="ma-4"
                                    label="Correct"
                                    data-cy="option2"
                            />
                            <v-textarea
                                    outline="textarea1"
                                    rows="10"
                                    v-model="editQuestion.options[1].content"
                                    label="Content 2"
                                    data-cy="content2"
                            ></v-textarea>
                        </v-flex>

                        <v-flex
                                xs24
                                sm12
                                md12
                        >
                            <v-switch
                                    v-model="editQuestion.options[2].correct"
                                    class="ma-4"
                                    label="Correct"
                                    data-cy="option3"
                            />
                            <v-textarea
                                    outline="textarea1"
                                    rows="10"
                                    v-model="editQuestion.options[2].content"
                                    label="Content 3"
                                    data-cy="content3"
                            ></v-textarea>
                        </v-flex>

                        <v-flex
                                xs24
                                sm12
                                md12
                        >
                            <v-switch
                                    v-model="editQuestion.options[3].correct"
                                    class="ma-4"
                                    label="Correct"
                                    data-cy="option4"
                            />
                            <v-textarea
                                    outline="textarea1"
                                    rows="10"
                                    v-model="editQuestion.options[3].content"
                                    label="Content 4"
                                    data-cy="content4"
                            ></v-textarea>
                        </v-flex>

                    </v-layout>
                </v-container>
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn color="blue darken-1" @click="$emit('dialog', false)"
                >Cancel</v-btn
                >
                <v-btn color="blue darken-1" @click="saveQuestion">Save</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script lang="ts">
  import { Component, Model, Prop, Vue } from 'vue-property-decorator';
  import Question from '@/models/management/Question';
  import RemoteServices from '@/services/RemoteServices';

  @Component
  export default class EditQuestionDialog extends Vue {
    @Model('dialog', Boolean) dialog!: boolean;
    @Prop({ type: Question, required: true }) readonly question!: Question;

    editQuestion!: Question;

    created() {
      this.editQuestion = new Question(this.question);
    }

    // TODO use EasyMDE with these configs
    // markdownConfigs: object = {
    //   status: false,
    //   spellChecker: false,
    //   insertTexts: {
    //     image: ['![image][image]', '']
    //   }
    // };

    async saveQuestion() {
      if (
        this.editQuestion &&
        (!this.editQuestion.title || !this.editQuestion.content)
      ) {
        await this.$store.dispatch(
          'error',
          'Question must have title and content'
        );
        return;
      }

      if (this.editQuestion && this.editQuestion.id != null) {
        try {
          const result = await RemoteServices.updateQuestion(this.editQuestion);
          this.$emit('save-question', result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      } else if (this.editQuestion) {
        try {
          const result = await RemoteServices.createQuestion(this.editQuestion);
          this.$emit('save-question', result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }
  }
</script>
