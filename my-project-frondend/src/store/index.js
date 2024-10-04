import { defineStore} from "pinia";

export const useStore = defineStore('store',{
    state:()=>{
        return {
            username: '',
            password: '',
            role: '',
            registerTime: null
        }
}
})