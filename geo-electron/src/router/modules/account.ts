export default {
  path: "/account",
  redirect: "/account/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "账号授权",
    rank:10
  },
  children: [
    {
      path: "/account/index",
      name: "Account",
      component: () => import("@/views/account/index.vue"),
      meta: {
        title: "账号授权"
      }
    },
  ]
} satisfies RouteConfigsTable;
