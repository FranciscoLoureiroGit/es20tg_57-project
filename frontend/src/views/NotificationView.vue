<template>
  <div>
    Notifications View
  </div>
</template>

<!--
<template>
  <v-menu bottom left offset-y origin="top right" transition="scale-transition">
    <template v-slot:activator="{ attrs, on }">
      <v-btn
        left
        style="padding-right: 2vh"
        class="ml-2"
        min-width="0"
        max-width="4vh"
        text
        v-bind="attrs"
        v-on="on"
      >
        <v-badge color="red" overlap>
          <template v-slot:badge>
            <span>{{ notifications.length }}</span>
          </template>
          <v-icon @click="drawer = true" color="white">mdi-bell</v-icon>
        </v-badge>
      </v-btn>
    </template>

    <v-list
      :tile="false"
      nav
      v-scroll
      width="25vh"
      max-height="40vh"
      v-if="notifications.length > 0"
    >
      <div>
        <app-bar-item v-for="(n, i) in notifications" :key="`item-${i}`">
          <v-card width="25vh" v-if="n.status === 'READ'" color="#E6E6E6" >
            <v-list-item>
              <v-icon>mdi-close</v-icon>
              <v-card-text align="left" class="font-weight-bold"><div>
                <i class="font-weight-light" style="font-size: 1vh">{{
                n.title
                  }}</i>
                {{
                n.description
                }}</div></v-card-text>
            </v-list-item>
          </v-card>
          <v-divider></v-divider>
        </app-bar-item>
        <app-bar-item v-for="(n, i) in notifications" :key="`item-${i}`">
          <v-card width="25vh" v-if="n.status === 'READ'">
            <v-list-item>
              <v-icon>mdi-close</v-icon>
              <v-card-text align="left">{{ n.title }}</v-card-text>
            </v-list-item>
          </v-card>
          <v-divider></v-divider>
        </app-bar-item>
        <li
          style="justify-content: space-between; display: flex;padding-top: 1vh"
        >
          <v-btn block
            ><b>Clear</b><v-icon>mdi-notification-clear-all</v-icon></v-btn
          >
        </li>
      </div>
    </v-list>

    <v-list
      :tile="false"
      nav
      max-width="25vh"
      max-height="40h"
      v-if="notifications.length == 0"
    >
      <div>
        <app-bar-item>
          <v-list-item>
            <v-card-text>You have no notifications</v-card-text>
          </v-list-item>
        </app-bar-item>
      </div>
    </v-list>
  </v-menu>
</template>
-->
<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class NotificationView extends Vue {

}
</script>

<style lang="scss" scoped />
