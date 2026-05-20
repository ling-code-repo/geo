export default {
  path: "/instruction",
  redirect: "/instruction/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "创作指令",
    rank: 7
  },
  children: [
    {
      path: "/instruction/index",
      name: "Instruction",
      component: () => import("@/views/instruction/index.vue"),
      meta: {
        title: "创作指令"
      }
    },
  ]
} satisfies RouteConfigsTable;
