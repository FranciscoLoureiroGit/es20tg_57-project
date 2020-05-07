export default class Notification {
  id: number | null = null;
  title: string = '';
  description: string = '';
  status: string = '';
  creationDate!: '';
  username: string | null = null;
  timeToDeliver!: '';
  urgent: boolean = false;

  constructor(jsonObj?: Notification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.description = jsonObj.description;
      this.creationDate = jsonObj.creationDate;
      this.status = jsonObj.status;
      this.username = jsonObj.username;
      this.timeToDeliver = jsonObj.timeToDeliver;
      this.urgent = jsonObj.urgent;
    }
  }
}
