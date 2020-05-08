<template>
<div>
    <v-list
            :three-line=true
            :shaped=true
            class="overflow-y-auto"
            >
        <v-card-title>Additional Discussion </v-card-title>
        <v-list-item-group color="primary">
            <v-list-item
                    v-for="(item, i) in this.clarification.extraClarificationDtos"
                    :key="i"

            >
                <v-list-item-content @click="showItem(item)">
                    <v-list-item-title align="left" v-html="item.commentType"/>
                    <v-list-item-subtitle align="left" v-html="item.comment"/>
                    <v-list-item-subtitle align="right" v-html="item.creationDate"/>
                </v-list-item-content>
            </v-list-item>
        </v-list-item-group>
    </v-list>
    <v-dialog
            v-if="showExtraClarificationDialog"
            v-model = "showExtraClarificationDialog"
            class="container"
            :value="dialog"
            @input="closeItem"
            @keydown.esc="closeItem"
            max-width="75%"
    >
        <v-card>
            <v-card-title>
                <span class="headline">{{ currentExtraClarification.commentType }}</span>
            </v-card-title>
            <v-card-text class="text-left">
                <div style="font-size: 18px">
                    <span v-html="currentExtraClarification.comment" />
                    <br />
                </div>
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn
                        data-cy="closeButton"
                        dark
                        color="blue darken-1"
                        @click="closeItem"
                >close</v-btn
                >
            </v-card-actions>
        </v-card>
    </v-dialog>
</div>


</template>

<script lang="ts">
  import { Component, Model, Prop, Vue } from 'vue-property-decorator';
  import ExtraClarification from '@/models/management/ExtraClarification';
  import Clarification from '../../../models/management/Clarification';

  @Component({
    components: {}
  })
  export default class ExtraClarificationListDialog extends Vue {
    @Model('dialog', Boolean) dialog!: boolean;
    @Prop({ type: Clarification, required: true })
    readonly clarification!: Clarification;

    currentExtraClarification: ExtraClarification | null = null;
    showExtraClarificationDialog: boolean = false;

    showItem(extraClarification: ExtraClarification){
      this.currentExtraClarification = extraClarification;
      this.showExtraClarificationDialog = true;
    }

    closeItem(){
      this.showExtraClarificationDialog = false;
    }

  }
</script>

<style lang="scss" scoped></style>